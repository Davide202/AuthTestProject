package com.example.test.config.basicauth;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
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
                        .contact(new Contact()
                                .name("Davide")
                                .email("davide@test.com")
                        )
                )
                .addSecurityItem(new SecurityRequirement()
                        .addList(BASIC_AUTH_SCHEME)
                )
                .components(new Components()
                        .addSecuritySchemes(
                                BASIC_AUTH_SCHEME,
                                new SecurityScheme()
                                    //.name(BASIC_AUTH_SCHEME)
                                    // Secondo le specifiche ufficiali di OpenAPI 3.0,
                                    // l'attributo name è valido soltanto quando il tipo di sicurezza è apiKey
                                    // (in cui il name rappresenta il nome reale dell'header o query param da cercare)
                                    .type(SecurityScheme.Type.HTTP)
                                    .scheme("basic")
                        )
                );
    }
}
