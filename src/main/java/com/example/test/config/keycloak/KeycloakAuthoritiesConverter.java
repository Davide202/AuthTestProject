package com.example.test.config.keycloak;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;


@Component
@Profile("keycloak")
public class KeycloakAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Value("${keycloak.security.client-id}")
    private String keycloakClientId;

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        // Estrai il map "realm_access"
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");

        if (realmAccess == null || realmAccess.isEmpty()) {
            return List.of();
        }

        // Estrai la lista "roles" dentro realm_access
        Collection<String> roles = (Collection<String>) realmAccess.get("roles");

        // Trasforma ogni stringa in un SimpleGrantedAuthority con prefisso ROLE_
        return this.extractRealmRoles(jwt)
                .stream()
                .map(roleName -> "ROLE_" + roleName)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    private Set<String> extractRealmRoles(Jwt jwt) {
        Object realmAccess = jwt.getClaim("realm_access");
        if (!(realmAccess instanceof Map<?, ?> map))
            return Set.of();
        Object roles = map.get("roles");
        if (!(roles instanceof Collection<?> coll))
            return Set.of();
        Set<String> out = new HashSet<>();
        for (Object r : coll)
            out.add(String.valueOf(r));
        return out;
    }


}