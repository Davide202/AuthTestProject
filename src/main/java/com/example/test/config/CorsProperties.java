package com.example.test.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.cors")
public class CorsProperties {

    // Impostiamo già dei default sensati nel caso in cui le variabili non vengano passate
    private List<String> allowedOrigins = List.of("*");

    private List<String> allowedMethods = List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH");

    private List<String> allowedHeaders = List.of("*");
}
