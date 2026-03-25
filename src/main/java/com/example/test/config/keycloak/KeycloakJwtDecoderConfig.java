package com.example.test.config.keycloak;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;


@Configuration
@Profile("keycloak")
@RequiredArgsConstructor
public class KeycloakJwtDecoderConfig {

    //@Bean
    public JwtDecoder jwtDecoder1(
            // 1. URL Interno per scaricare le chiavi (jwkSetUri)
            @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}") String jwkSetUri,
            // 2. URL Esterno per validare l'Issuer (issuerUri)
            @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}") String issuerUri
    ) {
//        String jwkSetUri = issuerUri.endsWith("/")
//                ? issuerUri + "protocol/openid-connect/certs"
//                : issuerUri + "/protocol/openid-connect/certs";

        // Spring scaricherà le chiavi in modo invisibile contattando il container "keycloak"
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();

        // Spring validerà che il token sia stato staccato da "localhost" (il browser)
        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuerUri);

        // Se vuoi essere ancora più permissivo (es. ignorare completamente l'issuer in dev):
        // OAuth2TokenValidator<Jwt> validator = new DelegatingOAuth2TokenValidator<>(new JwtTimestampValidator());
        // jwtDecoder.setJwtValidator(validator);

        jwtDecoder.setJwtValidator(withIssuer);
        return jwtDecoder;
    }

    @Bean
    public JwtDecoder jwtDecoder(
            @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}") String jwkSetUri,
            @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}") String issuerUri
    ) {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();

        // 1. Creiamo un validatore custom che accetta sia localhost (da Swagger) sia keycloak (da WSO2)
        OAuth2TokenValidator<Jwt> customIssuerValidator = jwt -> {
            String tokenIssuer = jwt.getIssuer() != null ? jwt.getIssuer().toString() : "";
            if (tokenIssuer.equals(issuerUri) || tokenIssuer.equals("http://keycloak:8081/realms/finmatica")) {
                return org.springframework.security.oauth2.core.OAuth2TokenValidatorResult.success();
            }
            return org.springframework.security.oauth2.core.OAuth2TokenValidatorResult.failure(
                    new org.springframework.security.oauth2.core.OAuth2Error("invalid_token", "Issuer non valido: " + tokenIssuer, null)
            );
        };

        // 2. Uniamo il nostro validatore con quello di Spring che controlla la scadenza del token (exp)
        OAuth2TokenValidator<Jwt> delegatingValidator = new org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator<>(
                new org.springframework.security.oauth2.jwt.JwtTimestampValidator(),
                customIssuerValidator
        );

        jwtDecoder.setJwtValidator(delegatingValidator);
        return jwtDecoder;
    }
}
