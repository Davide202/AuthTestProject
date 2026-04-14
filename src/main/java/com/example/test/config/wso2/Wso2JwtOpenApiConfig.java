package com.example.test.config.wso2;

import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

@Configuration
@Profile("wso2")
public class Wso2JwtOpenApiConfig {

    private static final String OAUTH_SCHEME = "Wso2OAuth";
    private static final String BEARER_SCHEME = "Wso2Bearer";

    @Value("${springdoc.swagger-ui.oauth.token-url}")
    private String serverUrl;

    @Bean
    public OpenAPI customOpenAPI() {

        String tokenUrl = String.format("%s/oauth2/token", serverUrl);

        return new OpenAPI()
                .info(new Info()
                        .title("WSO2 Protected API")
                        .version("1.0")
                        .description("API protette da WSO2 API Manager")
                        .contact(new Contact()
                                .name("Davide")
                                .email("davide@test.com")
                        )
                )
                // Richiediamo che almeno uno dei due metodi (OAuth o Bearer) sia utilizzato
                .addSecurityItem(new SecurityRequirement().addList(OAUTH_SCHEME))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME))
                .components(new Components()
                        .addSecuritySchemes(OAUTH_SCHEME, securitySchemeOauth())
                        .addSecuritySchemes(BEARER_SCHEME, securitySchemeBearer()));
    }

    private SecurityScheme securitySchemeOauth() {
        return new SecurityScheme()
                //.name(OAUTH_SCHEME)
                // Secondo le specifiche ufficiali di OpenAPI 3.0,
                // l'attributo name è valido soltanto quando il tipo di sicurezza è apiKey
                // (in cui il name rappresenta il nome reale dell'header o query param da cercare)
                .type(SecurityScheme.Type.OAUTH2)
                .flows(new OAuthFlows().password(
                        new OAuthFlow().tokenUrl(getTokenUrl()).scopes(new Scopes()))
                );
    }

    private SecurityScheme securitySchemeBearer() {
        return new SecurityScheme()
                //.name(BEARER_SCHEME)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer").bearerFormat("JWT");
    }

    private String getTokenUrl() {
        return String.format("%s/oauth2/token", serverUrl);
    }
}
