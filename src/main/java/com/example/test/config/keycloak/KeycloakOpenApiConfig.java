package com.example.test.config.keycloak;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("keycloak")
public class KeycloakOpenApiConfig {


    @Value("${springdoc.swagger-ui.oauth.token-url}")
    private String serverUrl;

    @Value("${keycloak.admin.realm}")
    private String realm;

    private static final String OAUTH_SCHEME_NAME = "KeycloakPasswordFlow";

    @Bean
    public OpenAPI customOpenAPI() {

        String tokenUrl = String.format("%s/realms/%s/protocol/openid-connect/token", serverUrl, realm);

        return new OpenAPI()
                .info(new Info().title("Finmatica API").version("1.0").description("API protette da Keycloak"))
                .addSecurityItem(new SecurityRequirement().addList(OAUTH_SCHEME_NAME))
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        OAUTH_SCHEME_NAME,
                                        new SecurityScheme()
                                                .name(OAUTH_SCHEME_NAME)
                                                .type(SecurityScheme.Type.OAUTH2)
                                                .flows(
                                                        new OAuthFlows()
                                                                .password(
                                                                        new OAuthFlow().tokenUrl(tokenUrl)
                                                                )
                                                )
                                )
                );
    }
}
