package com.example.test.util;


import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

/**
 * Retrieves the current {@link Jwt} from Spring Security context.
 *
 * @author Nexum Team
 * @version 1.0
 * @since 2024-01-28
 */
@Component
public class SecurityCurrentJwt {

    public Jwt get() {
        SecurityContext sc = SecurityContextHolder.getContext();
        if (sc != null) {
            var auth = sc.getAuthentication();
            if (auth != null && auth instanceof JwtAuthenticationToken token) {
                return token.getToken();
            }
        }
        return null;
    }

}
