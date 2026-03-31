package com.example.test.config.wso2;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Log4j2
@Profile("wso2")
@Configuration
@RequiredArgsConstructor
public class Wso2JwtAuthenticationConverterConfig {

    private final Wso2RestTemplateConfig wso2RestTemplateConfig;
    private final RestTemplate restTemplate;

    @Value("${app.wso2.user-info}")
    private String userInfoUrl;

    @Value("${app.wso2.use-default-rest-template}")
    private Boolean useDefaultRestTemplate;

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        // CACHE IN MEMORIA: Evita di fare la chiamata a WSO2 ad ogni singola request!
        // La chiave sarà il JTI (l'ID univoco del token) e il valore i ruoli.
        Map<String, Collection<GrantedAuthority>> roleCache = new ConcurrentHashMap<>();

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            String jti = jwt.getId(); // Estraiamo l'ID del token

            // 1. Controlliamo se abbiamo già i ruoli in cache per questo token
            /*if (jti != null && roleCache.containsKey(jti)) {
                return roleCache.get(jti);
            }*/

            Collection<GrantedAuthority> authorities = new ArrayList<>();
            String rawToken = jwt.getTokenValue();

            try {
                // 2. Prepariamo la chiamata verso l'API UserInfo di WSO2
                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(rawToken);
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                HttpEntity<String> entity = new HttpEntity<>(headers);

                // IMPORTANTE: Cambia l'URL se il tuo docker-compose usa un hostname diverso (es. api-manager)
                //String userInfoUrl = "https://localhost:9443/oauth2/userinfo";

                ResponseEntity<MapWrapper> response = this.getRestTemplate().exchange(
                        userInfoUrl + "?schema=openid",
                        HttpMethod.GET, entity, MapWrapper.class);

                MapWrapper userInfo = response.getBody();

                log.debug("---------------->> UserInfo :: {} <<----------------",userInfo);

                /*----------------------------------*/

                try{
//                    ResponseEntity<MapWrapper> response2 = this.getRestTemplate().exchange(
//                            "https://localhost:9443/scim2/Me",
//                            HttpMethod.GET, entity, MapWrapper.class);
//
//                    MapWrapper userInfo2 = response2.getBody();

                    //log.debug("---------------->> Scim2Me :: {} <<----------------",getUserInfoFromScim("testuser"));
                } catch (Exception e) {
                    log.error(e.getMessage());
                }


                /*----------------------------------*/

                String[] keys = {
                        "groups",
                        "role",
                        "roles",
                        "http://wso2.org/claims/role",
                        "http://wso2.org/claims/roles"
                };

                // 3. Estraiamo i ruoli.
                // ATTENZIONE: Controlla su Bruno se WSO2 usa "groups", "roles", o "http://wso2.org/claims/role"
                if (userInfo != null) {
                    List<String> roles = null;
                    for (String key : keys){
                        if (userInfo.containsKey(key)) {
                            roles = (List<String>) userInfo.get(key);
                            break;
                        }
                    }
//                    if (userInfo.containsKey("groups")) {
//                        roles = (List<String>) userInfo.get("groups");
//                    } else if (userInfo.containsKey("roles")) {
//                        roles = (List<String>) userInfo.get("roles");
//                    } else if (userInfo.containsKey("http://wso2.org/claims/role")) {
//                        roles = (List<String>) userInfo.get("http://wso2.org/claims/role");
//                    }

                    if (roles != null) {
                        for (String role : roles) {
                            // Puliamo stringhe del tipo "Internal/subscriber" in "ROLE_SUBSCRIBER"
                            String cleanRole = role.replace("Internal/", "").toUpperCase();
                            authorities.add(new SimpleGrantedAuthority("ROLE_" + cleanRole));
                        }
                    }
                }
            } catch (Exception e) {
                log.error("❌ Errore durante il recupero dei ruoli da UserInfo: {}" , e.getMessage());
            }

            // 4. Fallback: se l'utente non ha ruoli, assegniamo almeno il ROLE_USER di base
            if (authorities.isEmpty()) {
                authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            }

            // Salviamo il risultato in cache così le chiamate successive con lo stesso token saranno istantanee
            if (jti != null) {
                roleCache.put(jti, authorities);
            }

            return authorities;
        });

        return converter;
    }


    public String getUserInfoFromScim(String username) {
        // 1. Creiamo un RestTemplate "Insecure" (equivalente a --insecure in curl)
        RestTemplate restTemplate = getRestTemplate();

        // 2. Costruiamo l'URL dinamicamente in base allo username
        String url = "https://localhost:9443/scim2/Users?filter=userName+Eq+" + username;

        // 3. Impostiamo gli Header (Accept: application/json e Basic Auth admin:admin)
        HttpHeaders headers = new HttpHeaders();
        //headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headers.setBasicAuth("admin", "admin"); // Questo genera in automatico l'header Authorization: Basic...

        // 4. Prepariamo l'entità della richiesta
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // 5. Eseguiamo la chiamata GET
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            // Ritorniamo il JSON grezzo per analizzarlo
            return response.getBody();

        } catch (Exception e) {
            System.err.println("Errore durante la chiamata SCIM: " + e.getMessage());
            return null;
        }
    }

    private RestTemplate getRestTemplate() {
        if (Boolean.TRUE.equals(useDefaultRestTemplate))
            return this.restTemplate;
        return wso2RestTemplateConfig.restTemplate();
    }

    //    @Bean
//    public JwtAuthenticationConverter jwtAuthenticationConverter() {
//        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
//
//        // 1. Specifica il nome del claim nel token (WSO2 usa spesso "groups" o "roles")
//        grantedAuthoritiesConverter.setAuthoritiesClaimName("groups");
//
//        // 2. Aggiungi il prefisso "ROLE_" (Spring lo richiede per hasRole)
//        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
//
//        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
//        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
//        return jwtAuthenticationConverter;
//    }
}
