package com.example.test.config.basicauth;

import com.example.test.filters.ContextFilter;
import com.example.test.filters.JwtContextFilter;
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

import java.util.Arrays;

@Log4j2
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Profile("basicAuth")
@RequiredArgsConstructor
public class BasicAuthSecurityConfig {

    private final ContextFilter contextFilter;
    private final JwtContextFilter jwtContextFilter;

    @Value("${app.public-apis}")
    private String[] pubApisConfigured;

    @Value("${app.security.basic.username:admin}")
    private String basicUsername;

    @Value("${app.security.basic.password:password123}")
    private String basicPassword;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("Public apis {}", Arrays.toString(pubApisConfigured));
        http
                .csrf(AbstractHttpConfigurer::disable) // Spesso disabilitato per API stateless
                .authorizeHttpRequests(auth ->
                    auth
                    .requestMatchers(pubApisConfigured).permitAll()
                    .anyRequest().authenticated()
                )
                .addFilterBefore(contextFilter, BasicAuthenticationFilter.class)
                .addFilterAfter(jwtContextFilter, BasicAuthenticationFilter.class)
                .httpBasic(Customizer.withDefaults()); // Attiva la Basic Auth

        return http.build();
    }
    @Bean
    public UserDetailsService userDetailsService() {
        // 2. USO DELLE VARIABILI INIETTATE PER CREARE L'UTENTE
        UserDetails user = User.builder()
                .username(basicUsername)
                .password(passwordEncoder().encode(basicPassword))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user);
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
