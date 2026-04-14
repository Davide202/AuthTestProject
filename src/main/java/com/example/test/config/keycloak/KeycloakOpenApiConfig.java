package com.example.test.config.keycloak;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("keycloak")
@RequiredArgsConstructor
public class KeycloakOpenApiConfig {

    private static final String OAUTH_SCHEME_NAME = "KeycloakPasswordFlow";
    @Value("${springdoc.swagger-ui.oauth.token-url}")
    private String serverUrl;
    private final KeycloakAdminProperties properties;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                    .title("Finmatica API")
                    .version("1.0")
                    .description("API protette da Keycloak")
                    .contact(new Contact()
                            .name("Davide")
                            .email("davide@test.com")
                    )
            )
            .addSecurityItem(new SecurityRequirement().addList(OAUTH_SCHEME_NAME))
            .components(new Components().addSecuritySchemes(OAUTH_SCHEME_NAME, securitySchemeOauth()));
    }

    private SecurityScheme securitySchemeOauth(){
        return new SecurityScheme()
                //.name(OAUTH_SCHEME_NAME)
                // Secondo le specifiche ufficiali di OpenAPI 3.0,
                // l'attributo name è valido soltanto quando il tipo di sicurezza è apiKey
                // (in cui il name rappresenta il nome reale dell'header o query param da cercare)
                .type(SecurityScheme.Type.OAUTH2)
                .flows(new OAuthFlows().password(
                        new OAuthFlow().tokenUrl(getTokenUrl()).scopes(new Scopes())
                        )
                );
    }

    private String getTokenUrl(){
        String realm = properties.getRealm();
        return String.format("%s/realms/%s/protocol/openid-connect/token", serverUrl, realm);
    }
}
