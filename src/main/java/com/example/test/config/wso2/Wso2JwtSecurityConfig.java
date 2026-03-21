package com.example.test.config.wso2;

import com.example.test.config.CorsProperties; // Assicurati che il path sia corretto
import com.example.test.filters.ContextFilter;
import com.example.test.filters.JwtContextFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;

/**
 * # L'URL deve puntare all'endpoint di WSO2 che espone le chiavi (solitamente /oauth2/jwks)
 * spring.security.oauth2.resourceserver.jwt.jwk-set-uri=https://<wso2-host>:<port>/oauth2/jwks
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Profile("wso2")
@RequiredArgsConstructor
public class Wso2JwtSecurityConfig {

    private final ContextFilter contextFilter;
    private final JwtContextFilter jwtContextFilter;

    // 1. INIETTIAMO LE PROPRIETA' CORS (come fatto per Keycloak)
    private final CorsProperties corsProperties;

    // 2. AGGIUNGIAMO IL BEAN PER I CORS
    @Bean
    UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOriginPatterns(corsProperties.getAllowedOrigins());
        configuration.setAllowedMethods(corsProperties.getAllowedMethods());
        configuration.setAllowedHeaders(corsProperties.getAllowedHeaders());

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain jwtFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)

                // 3. ABILITIAMO I CORS NELLA FILTER CHAIN
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // --- ATTENZIONE QUI ---
                // Ho commentato il securityMatcher perché andava in conflitto con Swagger!
                // .securityMatcher("/api/**")

                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers(
                                        "/public/**",
                                        "/actuator/health",
                                        "/actuator/info",
                                        "/v3/api-docs/**",
                                        "/swagger-ui/**",
                                        "/swagger-ui.html"
                                ).permitAll()
                                .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        // 4. FIX: Hai creato il converter in basso, ma non lo stavi usando!
                        // Usando Customizer.withDefaults() Spring ignorava il tuo converter per WSO2.
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                )
                .addFilterBefore(contextFilter,     BearerTokenAuthenticationFilter.class)
                .addFilterAfter(jwtContextFilter,   BearerTokenAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

        // 1. Specifica il nome del claim nel token (WSO2 usa spesso "groups" o "roles")
        grantedAuthoritiesConverter.setAuthoritiesClaimName("groups");

        // 2. Aggiungi il prefisso "ROLE_" (Spring lo richiede per hasRole)
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }
}