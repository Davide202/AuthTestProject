package com.example.test.filters;

import com.example.test.config.ContextWrapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
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
    private static final String CID = "cid";


    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException
    {
        MDC.put(CID,UUID.randomUUID().toString());
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
