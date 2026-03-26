package com.example.test.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value = { "federationLink" }, ignoreUnknown = true)
public abstract class CredentialMixIn {
}
