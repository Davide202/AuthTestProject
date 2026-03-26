package com.example.test.config.wso2;

import com.example.test.filters.ContextFilter;
import com.example.test.filters.SecurityContextFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;

import java.util.Arrays;

/**
 * # L'URL deve puntare all'endpoint di WSO2 che espone le chiavi (solitamente /oauth2/jwks)
 * spring.security.oauth2.resourceserver.jwt.jwk-set-uri=https://<wso2-host>:<port>/oauth2/jwks
 */
@Log4j2
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Profile("wso2")
@RequiredArgsConstructor
public class Wso2JwtSecurityConfig {

    private final ContextFilter contextFilter;
    private final SecurityContextFilter securityContextFilter;
    private final JwtAuthenticationConverter jwtAuthenticationConverter;
    private final UrlBasedCorsConfigurationSource corsConfigurationSource;

    @Value("${app.public-apis}")
    private String[] pubApisConfigured;

    @Bean
    public SecurityFilterChain jwtFilterChain(HttpSecurity http) throws Exception {
        log.info("Public apis {}", Arrays.toString(pubApisConfigured));
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .authorizeHttpRequests(auth ->
                        auth
                            .requestMatchers(pubApisConfigured).permitAll()
                            .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth ->
                        oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)))
                .addFilterBefore(contextFilter,     BearerTokenAuthenticationFilter.class)
                .addFilterAfter(securityContextFilter,   BearerTokenAuthenticationFilter.class)
        ;
        return http.build();
    }



}