package com.example.test.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObjectMapperConfiguration {

    @Bean
    public ObjectMapper objectMapper(){
        return JsonMapper.builder()
                .enable(StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .serializationInclusion(JsonInclude.Include.NON_NULL)
                .addMixIn(org.keycloak.representations.idm.CredentialRepresentation.class, CredentialMixIn.class)
                .build()
                .registerModule(new JavaTimeModule())
                .registerModule(new ParameterNamesModule())
                .registerModule(new Jdk8Module())
                ;


    }
}
