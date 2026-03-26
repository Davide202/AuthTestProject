package com.example.test.config.keycloak;


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


@Log4j2
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Profile("keycloak")
@RequiredArgsConstructor
public class KeycloakSecurityConfig {

    private final ContextFilter contextFilter;
    private final SecurityContextFilter securityContextFilter;
    private final JwtAuthenticationConverter jwtAuthenticationConverter;
    private final UrlBasedCorsConfigurationSource corsConfigurationSource;

    @Value("${app.public-apis}")
    private String[] pubApisConfigured;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("Public apis {}", Arrays.toString(pubApisConfigured));
        http
                .csrf(AbstractHttpConfigurer::disable)
               .cors( cors -> cors.configurationSource(corsConfigurationSource))
                .authorizeHttpRequests(auth ->
                    auth
                    .requestMatchers(pubApisConfigured).permitAll()
                    .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth ->
                        oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)))
                .addFilterBefore(contextFilter, BearerTokenAuthenticationFilter.class)
                .addFilterAfter(securityContextFilter, BearerTokenAuthenticationFilter.class)
        ;
        return http.build();
    }


}
