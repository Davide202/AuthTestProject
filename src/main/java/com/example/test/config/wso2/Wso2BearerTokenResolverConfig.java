package com.example.test.config.wso2;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;


@Configuration
@Profile("wso2")
@RequiredArgsConstructor
public class Wso2BearerTokenResolverConfig {

    /* Questo bean modifica l'header dal quale si estrae il token JWT */
    @Bean
    public BearerTokenResolver wso2BearerTokenResolver() {
        //return new HeaderBearerTokenResolver("x-jwt-assertion");
        return new DefaultBearerTokenResolver();
    }
}
