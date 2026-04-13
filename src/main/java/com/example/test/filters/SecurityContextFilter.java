package com.example.test.filters;



import com.example.test.context.AppContextScopedValue;
import com.example.test.context.RequestContextDataBuilder;
import com.example.test.util.SecurityContextUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.slf4j.MDC;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


@Log4j2
@Component
@RequiredArgsConstructor
public class SecurityContextFilter extends OncePerRequestFilter {

    private static final String CID = "cid";
    private static final String TENANT_HEADER = "X-Tenant-Id";
    private static final String X_JWT_ASSERTION = "x-jwt-assertion";
    private static final String USERNAME = "X-User-Name";

    private final SecurityContextUtil securityContextUtil;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        if (request.getRequestURI().contains("/actuator/health")){
            filterChain.doFilter(request, response);
            return;
        }
        String cid = MDC.get(CID);

        Jwt jwt = securityContextUtil.getJwt();
        if (jwt != null){
            log.info("JWT Authentication principal subject [{}] claims [{}]",jwt.getSubject(),jwt.getClaims());
        }
        List<String> roles = securityContextUtil.extractRolesFromSecurityContext();
        log.info("Roles in SecurityContext: {}", roles);

        String tenantHeader = request.getHeader(TENANT_HEADER);
        String wso2header = request.getHeader(X_JWT_ASSERTION);
        String username = request.getHeader(USERNAME);


        ScopedValue
                .where(
                        AppContextScopedValue.REQUEST_CONTEXT,
                        RequestContextDataBuilder.builder()
                                .traceId(cid)
                                .roles(roles)
                                .tenantId(tenantHeader)
                                .username(username)
                                .wso2header(wso2header)
                                .build()
                )
                .run(() -> {
                    try {
                        filterChain.doFilter(request, response);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}
