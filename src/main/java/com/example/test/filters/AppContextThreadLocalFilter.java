package com.example.test.filters;

import com.example.test.context.AppContextThreadLocal;
import com.example.test.util.SecurityContextUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
public class AppContextThreadLocalFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;
    //private final SecurityContextUtil securityContextUtil; NON UTILIZZARE!!!

    private static final String CID = "cid";
    public static final String TENANT_HEADER = "X-Tenant-Id";
    public static final String X_JWT_ASSERTION = "x-jwt-assertion";
    public static final String USERNAME = "X-User-Name";


    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException
    {
        if (request.getRequestURI().contains("/actuator/health")){
            filterChain.doFilter(request, response);
            return;
        }
        MDC.put(CID,UUID.randomUUID().toString());
        printAuthenticationHeader(request);

        String tenantHeader = request.getHeader(TENANT_HEADER);
        AppContextThreadLocal.setTenantId(tenantHeader);

        String wso2header = request.getHeader(X_JWT_ASSERTION);
        AppContextThreadLocal.setXJwtAssertion(wso2header);

        String username = request.getHeader(USERNAME);
        AppContextThreadLocal.setUsername(username);

        try {
            filterChain.doFilter(request, response);
        } finally {
            AppContextThreadLocal.clear();
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
