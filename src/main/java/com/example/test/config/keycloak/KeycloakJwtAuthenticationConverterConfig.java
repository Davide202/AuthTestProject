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

    private final KeycloakAuthoritiesConverter converter;

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        var jac = new JwtAuthenticationConverter();
        jac.setPrincipalClaimName("sub");
        jac.setJwtGrantedAuthoritiesConverter(this.converter);
        return jac;
    }
}
