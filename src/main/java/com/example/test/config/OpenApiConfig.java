package com.example.test.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import java.util.List;


//@OpenAPIDefinition
//@Configuration
public class OpenApiConfig /*implements WebMvcConfigurer*/{

    @Autowired
    EnvUtil envUtil;

    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI()
                .servers(List.of(
                        new Server().url(envUtil.getServerUrlPrefix()).description("Server")
                ))
                .info(new Info()
                        .title("API del mio Progetto")
                        .version("1.0")
                        .description("Documentazione interattiva per il mio servizio Spring Boot"))
//                .components(new Components()
//                        .addSecuritySchemes("bearerAuth",
//                                new SecurityScheme()
//                                        .type(SecurityScheme.Type.HTTP)
//                                        .scheme("bearer")
//                                        .bearerFormat("JWT")))
//                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                ;
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .pathsToMatch("/**")
                .build();
    }
}
