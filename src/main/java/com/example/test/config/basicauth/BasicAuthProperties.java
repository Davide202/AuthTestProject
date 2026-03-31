package com.example.test.config.basicauth;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Data
@Configuration
@Profile("basicAuth")
@ConfigurationProperties(prefix = "app.basic-security")
public class BasicAuthProperties {

    private String username;
    private String password;
    private String[] roles;

}
