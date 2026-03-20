package com.example.test.filters;

import com.example.test.config.ContextWrapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter to extract the X-Tenant-Id header and populate TenantContext.
 * This ensures all down-stream logic can access the tenant ID without
 * passing it manually.
 */
@Component
@RequiredArgsConstructor
public class ContextFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(ContextFilter.class);
    private static final String TENANT_HEADER = "X-Tenant-Id";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

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

        MDC.put("cid",UUID.randomUUID().toString());

        String tenantHeader = request.getHeader(TENANT_HEADER);

        if (tenantHeader != null && !tenantHeader.isBlank()) {
            try {
                UUID tenantId = UUID.fromString(tenantHeader);
                ContextWrapper.setTenantId(tenantId);
                log.debug("Set TenantContext from header: {}", tenantId);
            } catch (IllegalArgumentException e) {
                log.warn("Invalid UUID format in X-Tenant-Id header: {}", tenantHeader);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid X-Tenant-Id format");
                return;
            }
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            ContextWrapper.clear();
            log.trace("Cleared TenantContext");
        }
    }
}
