package com.example.test.config.keycloak;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

@Configuration
@Profile("keycloak")
@RequiredArgsConstructor
public class KeycloakJwtAuthenticationConverterConfig {

    private final KeycloakAuthoritiesConverter keycloakAuthoritiesConverter;

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        // Colleghiamo il nostro estrattore personalizzato
        converter.setJwtGrantedAuthoritiesConverter(keycloakAuthoritiesConverter);

        return converter;
    }
}
