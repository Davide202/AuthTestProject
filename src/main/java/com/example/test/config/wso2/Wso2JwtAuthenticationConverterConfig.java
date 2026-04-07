package com.example.test.config.wso2;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;


@Log4j2
@Profile("wso2")
@Configuration
@RequiredArgsConstructor
public class Wso2JwtAuthenticationConverterConfig {

    private final Wso2AuthoritiesConverter converter;

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter2() {
        var jac = new JwtAuthenticationConverter();
        jac.setPrincipalClaimName("sub");
        jac.setJwtGrantedAuthoritiesConverter(converter);
        return jac;
    }
}
