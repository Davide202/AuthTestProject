package com.example.test.config.basicauth;

import com.example.test.filters.ContextFilter;
import com.example.test.filters.SecurityContextFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Log4j2
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Profile("basicAuth")
@RequiredArgsConstructor
public class BasicAuthSecurityConfig {

    private final ContextFilter contextFilter;
    private final SecurityContextFilter securityContextFilter;
    private final BasicAuthProperties properties;
    private final UrlBasedCorsConfigurationSource corsConfigurationSource;

    @Value("${app.public-apis}")
    private String[] pubApisConfigured;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("Public apis {}", Arrays.toString(pubApisConfigured));
        http
                .csrf(AbstractHttpConfigurer::disable) // Spesso disabilitato per API stateless
                .cors( cors -> cors.configurationSource(corsConfigurationSource))
                .authorizeHttpRequests(auth ->
                    auth
                    .requestMatchers(pubApisConfigured).permitAll()
                    .anyRequest().authenticated()
                )
                .addFilterBefore(contextFilter, BasicAuthenticationFilter.class)
                .addFilterAfter(securityContextFilter, BasicAuthenticationFilter.class)
                .httpBasic(Customizer.withDefaults()); // Attiva la Basic Auth

        return http.build();
    }
    @Bean
    public UserDetailsService userDetailsService() {

        String[] roles = Arrays.stream(properties.getRoles())
            .map(r -> r.replace("ROLE_",""))
            .toArray(String[]::new);
        UserDetails user = User.builder()
                .username(properties.getUsername())
                .password(passwordEncoder().encode(properties.getPassword()))
                .roles(roles)
                .build();

        return new InMemoryUserDetailsManager(user);
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
