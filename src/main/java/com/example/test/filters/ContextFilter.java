package com.example.test.filters;

import com.example.test.config.ContextWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.shaded.gson.JsonObject;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
@Log4j2
@Component
@RequiredArgsConstructor
public class ContextFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;
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
        printAuthenticationHeader(request);
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

    private void printAuthenticationHeader(HttpServletRequest request){
        try {
            StringJoiner joiner = new StringJoiner(" ," );
            request.getHeaderNames().asIterator().forEachRemaining(h -> {
                String v = request.getHeader(h);
                joiner.add(h + " :: " + v);
            });
            log.info("Headers :: {}",joiner.toString());
            request.getHeaderNames().asIterator().forEachRemaining(h -> {
                String v = request.getHeader(h);
                log.info("{} :: {}",h,v);
                if ("authorization".equalsIgnoreCase(h) && v.contains("Bearer ")){
                    String jwt = v.replace("Bearer ","");
                    String body = jwt.split("\\.")[1];
                    byte[] decBody = Base64.getDecoder().decode(body.getBytes(StandardCharsets.UTF_8));
                    String decJwt = new String(decBody,StandardCharsets.UTF_8);
                    log.info("JWT BODY :: {}",decJwt);
                    try {
                        long exp = objectMapper.readTree(decJwt).findPath("exp").asLong();
                        log.info("JWT expires at {}",new Date(exp*1000));
                    } catch (JsonProcessingException e) {
                        log.error(e);
                    }
                }
            });

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
