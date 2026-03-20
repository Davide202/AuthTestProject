package com.example.test.config.keycloak;



import com.example.test.config.CorsProperties;
import com.example.test.filters.ContextFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Profile("keycloak") //java -jar app.jar --spring.profiles.active=keycloak
@RequiredArgsConstructor
public class KeycloakSecurityConfig {

    private final ContextFilter tenantFilter;
    private final CorsProperties corsProperties;


    @Bean
    public JwtDecoder jwtDecoder(@Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}") String issuerUri) {
        String jwkSetUri = issuerUri.endsWith("/")
                ? issuerUri + "protocol/openid-connect/certs"
                : issuerUri + "/protocol/openid-connect/certs";

        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();

        // Creiamo un validatore delegato
        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuerUri);

        // Se vuoi essere ancora più permissivo (es. ignorare completamente l'issuer in dev):
        // OAuth2TokenValidator<Jwt> validator = new DelegatingOAuth2TokenValidator<>(new JwtTimestampValidator());
        // jwtDecoder.setJwtValidator(validator);

        jwtDecoder.setJwtValidator(withIssuer);
        return jwtDecoder;
    }


    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter(
            KeycloakAuthoritiesConverter keycloakAuthoritiesConverter
    ) {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        // Colleghiamo il nostro estrattore personalizzato
        converter.setJwtGrantedAuthoritiesConverter(keycloakAuthoritiesConverter);

        return converter;
    }

    @Bean
    UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Leggiamo i valori dal bean tipizzato
        configuration.setAllowedOriginPatterns(corsProperties.getAllowedOrigins());
        configuration.setAllowedMethods(corsProperties.getAllowedMethods());
        configuration.setAllowedHeaders(corsProperties.getAllowedHeaders());

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationConverter jwtAuthenticationConverter,
            UrlBasedCorsConfigurationSource corsConfigurationSource
    ) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
               .cors( cors -> cors.configurationSource(corsConfigurationSource))
                .authorizeHttpRequests(auth ->
                    auth
                        .requestMatchers(
                                "/public/**",
                                "/actuator/health",
                                "/actuator/info",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // We expose an informational endpoint about auth flows.
                        .requestMatchers(HttpMethod.GET, "/api/v1/auth/info").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth ->
                        oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)))
                .addFilterAfter(tenantFilter, org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter.class)
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }


}
