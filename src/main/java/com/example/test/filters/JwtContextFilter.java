package com.example.test.filters;



import com.example.test.config.ContextWrapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


@Log4j2
@Component
public class JwtContextFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        SecurityContext securityContext = SecurityContextHolder.getContext();
        if (securityContext != null) {
            Authentication authentication = securityContext.getAuthentication();
            if (authentication != null){
                Object principal = authentication.getPrincipal();
                if (principal != null && principal instanceof Jwt jwt) {
                    log.info("Authentication principal subject is Jwt {}",jwt.getSubject());
                    log.info("Authentication principal claims is Jwt {}",jwt.getClaims());
                }
                var ga = authentication.getAuthorities();
                if (ga != null && !ga.isEmpty()){
                    List<String> roles = ga.stream().map(GrantedAuthority::getAuthority).toList();
                    ContextWrapper.put("roles", roles);
                    log.info("Roles in Jwt: {}", roles);
                }else{
                    log.info("No Roles in Jwt");
                }
            }
        }
        filterChain.doFilter(request, response);
        // Non serve il finally qui se pulisci già tutto nel TenantFilter che sta più "in alto" nella catena
    }
}
