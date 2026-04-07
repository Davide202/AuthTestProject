package com.example.test.config.keycloak;


import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Configuration
@Profile("keycloak")
public class KeycloakAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private static final String ROLE_PREFIX = "ROLE_";
    private static final String REALM_ACCESS = "realm_access";
    private static final String ROLES = "roles";

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        // Estrai il map "realm_access"
        Map<String, Object> realmAccess = jwt.getClaim(REALM_ACCESS);

        if (realmAccess == null || realmAccess.isEmpty()) {
            return List.of();
        }

        // Trasforma ogni stringa in un SimpleGrantedAuthority con prefisso ROLE_
        return this.extractRealmRoles(jwt)
                .stream()
                .map(roleName -> roleName.contains(ROLE_PREFIX) ? roleName :  ROLE_PREFIX + roleName)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    private Set<String> extractRealmRoles(Jwt jwt) {
        Object realmAccess = jwt.getClaim(REALM_ACCESS);
        if (!(realmAccess instanceof Map<?, ?> map))
            return Set.of();
        Object roles = map.get(ROLES);
        if (!(roles instanceof Collection<?> coll))
            return Set.of();
        Set<String> out = new HashSet<>();
        for (Object r : coll)
            out.add(String.valueOf(r));
        return out;
    }


}