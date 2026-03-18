package com.example.test.config.keycloak;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.plugins.providers.jackson.ResteasyJackson2Provider;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.concurrent.TimeUnit;

@Configuration
@Profile("keycloak")
public class KeycloakAdminConfig {

    @Value("${finmatica.keycloak.admin.server-url}")
    private String serverUrl;

    @Value("${finmatica.keycloak.admin.realm}")
    private String realm;

    @Value("${finmatica.keycloak.admin.client-id}")
    private String clientId;

    @Value("${finmatica.keycloak.admin.client-secret}")
    private String clientSecret;

    @Autowired
    ObjectMapper objectMapper;

    @Bean
    public Keycloak keycloakAdminClient() {

//        ObjectMapper objectMapper = JsonMapper.builder()
//                .enable(StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION)
//                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
//                .serializationInclusion(JsonInclude.Include.NON_NULL)
//                .addMixIn(org.keycloak.representations.idm.CredentialRepresentation.class, CredentialMixIn.class)
//                .build();

        ResteasyJackson2Provider jacksonProvider = new ResteasyJackson2Provider();
        jacksonProvider.setMapper(objectMapper);

        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)//only password and client_credentials are supported
                .clientId(clientId)
                .clientSecret(clientSecret)
                .resteasyClient(
                        ResteasyClientBuilder.newBuilder()
                                .register(jacksonProvider,100)
                                .connectTimeout(30L, TimeUnit.SECONDS)
                                .build())
                .build();
    }

}
