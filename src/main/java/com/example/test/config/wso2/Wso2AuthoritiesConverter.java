package com.example.test.config.wso2;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import  org.springframework.core.convert.converter.Converter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.client.RestTemplate;

import java.util.*;


@Log4j2
@Profile("wso2")
@Configuration
@RequiredArgsConstructor
public class Wso2AuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {


    private final Wso2RestTemplateConfig wso2RestTemplateConfig;

    @Value("${app.wso2.user-info}")
    private String userInfoUrl;

    private static final String[] ROLE_KEYS = {
            "groups",
            "role",
            "roles",
            "http://wso2.org/claims/role",
            "http://wso2.org/claims/roles"
    };

    private final Boolean USE_INFO_USER_API = Boolean.FALSE;
    private final Boolean USE_SCIM2_USERS_API = Boolean.FALSE;

    @Override
    public Collection<GrantedAuthority> convert( Jwt jwt) {

        List<GrantedAuthority> authorities = new ArrayList<>();
        if (jwt == null) return authorities;
        this.addRolesFromUserInfoApi(jwt,authorities);
        this.getUserInfoFromScim(jwt, authorities);
        this.addRoles(jwt.getClaims(),authorities);

        return authorities.stream().distinct().toList();
    }

    private void addRolesFromUserInfoApi(Jwt jwt, List<GrantedAuthority> authorities) {
        if (Boolean.FALSE.equals(USE_INFO_USER_API)) return;
        try {
            String rawToken = jwt.getTokenValue();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(rawToken);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            HttpEntity<String> entity = new HttpEntity<>(headers);
            String url = userInfoUrl + "?schema=openid";
            ResponseEntity<MapWrapper> response =
                    wso2RestTemplateConfig.getRestTemplate().exchange(url, HttpMethod.GET, entity, MapWrapper.class);
            MapWrapper userInfo = response.getBody();
            log.debug("---------------->> UserInfo :: {} <<----------------",response);
            this.addRoles(userInfo,authorities);
        } catch (Exception e) {
            log.error("❌ Errore durante il recupero dei ruoli da UserInfo: {}" , e.getMessage());
        }
    }

    public void getUserInfoFromScim(Jwt jwt, List<GrantedAuthority> authorities) {
        if (Boolean.FALSE.equals(USE_SCIM2_USERS_API)) return;
        String username = jwt.getSubject();
        String url = "https://localhost:9443/scim2/Users?filter=userName+Eq+" + username;
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headers.setBasicAuth("admin", "admin"); // Questo genera in automatico l'header Authorization: Basic...
        HttpEntity<String> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<MapWrapper> response = wso2RestTemplateConfig.getRestTemplate().exchange(url, HttpMethod.GET, entity, MapWrapper.class);
            log.debug("---------------->> ScimInfo :: {} <<----------------",response);
            this.addRoles(response.getBody(),authorities);

        } catch (Exception e) {
            log.error("Errore durante la chiamata SCIM: {}" , e.getMessage());
        }
    }

    private void addRoles(Map<String,Object> map, List<GrantedAuthority> authorities) {
        // 3. Estraiamo i ruoli.
        // ATTENZIONE: Controlla su Bruno se WSO2 usa "groups", "roles", o "http://wso2.org/claims/role"
        if (map != null) {
            Set<String> roles = new HashSet<>();
            for (String key : ROLE_KEYS){
                if (map.containsKey(key)) {
                    if (map.get(key) instanceof List list && !list.isEmpty()){
                        for (Object o : list){
                            if (o instanceof String s){
                                roles.add(s);
                            }
                        }
                        break;
                    }else if (map.get(key) instanceof String s){
                        if (!s.isBlank()){
                            roles.add(s);
                        }
                        break;
                    }
                }
            }
            for (String role : roles) {
                // Puliamo stringhe del tipo "Internal/subscriber" in "ROLE_SUBSCRIBER"
                String cleanRole = this.cleanWso2Roles(role);
                if (cleanRole != null && ! cleanRole.isBlank()){
                    SimpleGrantedAuthority sga = new SimpleGrantedAuthority("ROLE_" + cleanRole);
                    authorities.add(sga);
                }
            }
        }
    }

    private String cleanWso2Roles(String role){
        if (role == null) return null;
        String cleanRole;
        if (role.contains("/")) {
            cleanRole = role.substring(role.lastIndexOf("/") + 1);
        }else {
            cleanRole = role;
        }
        if (!cleanRole.isBlank()){
            cleanRole = cleanRole.toUpperCase();
        }
        return cleanRole;
    }


}
