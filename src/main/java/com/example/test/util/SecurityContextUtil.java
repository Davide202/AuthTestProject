package com.example.test.util;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;



@Component
public class SecurityContextUtil {

    public Jwt getJwt() {
        SecurityContext sc = SecurityContextHolder.getContext();
        if (sc != null) {
            var auth = sc.getAuthentication();
            if (auth != null && auth instanceof JwtAuthenticationToken token) {
                return token.getToken();
            }
        }
        return null;
    }

    public List<String> extractRolesFromSecurityContext() {

        List<String> roles = new ArrayList<>();
        SecurityContext securityContext = SecurityContextHolder.getContext();
        if (securityContext != null) {
            Authentication authentication = securityContext.getAuthentication();
            if (authentication != null){
                var ga = authentication.getAuthorities();
                if (ga != null && !ga.isEmpty()){
                    for (var auth : ga){
                        roles.add(auth.getAuthority());
                    }
                }
            }
        }
        return roles;
    }
}
