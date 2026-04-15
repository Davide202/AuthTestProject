package com.example.test.config.wso2;


import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.proc.DefaultJOSEObjectTypeVerifier;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;


@Configuration
@Profile("wso2")
@RequiredArgsConstructor
public class Wso2JwtDecoderConfig {

    private final Wso2RestTemplateConfig wso2RestTemplateConfig;


    @Bean
    public JwtDecoder jwtDecoder(
            @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}") String jwkSetUri,
            @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}") String issuerUri
    ) {
        NimbusJwtDecoder jwtDecoder = this.nimbusJwtDecoder(jwkSetUri,"jwt","at+jwt");
                //NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();

        OAuth2TokenValidator<Jwt> customIssuerValidator = jwt -> {
            String tokenIssuer = jwt.getIssuer() != null ? jwt.getIssuer().toString() : "";
            // Accettiamo sia il nome interno di Docker che l'URL generato da WSO2 per i client esterni
            if (tokenIssuer.equals(issuerUri) || tokenIssuer.equals("https://localhost:9443/oauth2/token")) {
                return OAuth2TokenValidatorResult.success();
            }
            return OAuth2TokenValidatorResult.failure(
                    new OAuth2Error("invalid_token", "Issuer non valido: " + tokenIssuer, null)
            );
        };

        OAuth2TokenValidator<Jwt> delegatingValidator = new DelegatingOAuth2TokenValidator<>(
                new JwtTimestampValidator(),
                customIssuerValidator
        );

        jwtDecoder.setJwtValidator(delegatingValidator);
        return jwtDecoder;
    }

    private NimbusJwtDecoder nimbusJwtDecoder(String jwkSetUri,String... allowedTypes){
        return NimbusJwtDecoder.withJwkSetUri(jwkSetUri)
                .restOperations(wso2RestTemplateConfig.getRestTemplate())
                .jwtProcessorCustomizer(customizer -> {
                    customizer.setJWSTypeVerifier(this.defaultJOSEObjectTypeVerifier(allowedTypes));
                }).build();
    }

    private DefaultJOSEObjectTypeVerifier defaultJOSEObjectTypeVerifier(String... allowedTypes){
        List<JOSEObjectType> list = new ArrayList<>();
        for (String s : allowedTypes) list.add(new JOSEObjectType(s));
        list.add(null);
        return new DefaultJOSEObjectTypeVerifier<>(list.toArray(JOSEObjectType[]::new));
    }


}
