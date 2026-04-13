package com.example.test.config.basicauth;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Map;


@Configuration
@Profile("basicAuth")
public class BasicAuthOpenApiConfig {

    private static final String BASIC_AUTH_SCHEME = "basicAuth";


    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Basic Auth API")
                        .version("1.0")
                        .description("API protette da Basic Authentication")
                        .extensions(Map.of("key","value"))
                )
                // Richiede l'autenticazione per tutte le API documentate
                .addSecurityItem(new SecurityRequirement().addList(BASIC_AUTH_SCHEME))
                .components(new Components()
                        // Definisce lo schema di tipo HTTP Basic
                        .addSecuritySchemes(BASIC_AUTH_SCHEME, new SecurityScheme()
                                .name(BASIC_AUTH_SCHEME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("basic")));
    }
}
