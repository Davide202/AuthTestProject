package com.example.test.context;

import com.example.test.filters.AppContextThreadLocalFilter;
import lombok.extern.log4j.Log4j2;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Log4j2
public class AppContextThreadLocal {

    private static final ThreadLocal<Map<String,Object>> context = new ThreadLocal<>();

    private static void put(String key, Object value) {
        Map<String,Object> map = context.get();
        if (map == null) {map = new HashMap<>();}
        map.put(key, value);
        context.set(map);
    }

    private static Object get(String key) {
        Map<String,Object> map = context.get();
        if (map == null) {map = new HashMap<>();}
        return  map.get(key);
    }

    public static void clear() {
        context.remove();
    }

    public static void setTenantId(UUID tenantId) {
        put(AppContextThreadLocalFilter.TENANT_HEADER, tenantId);
    }

    public static void setTenantId(String tenantHeader) {
        if (tenantHeader == null || tenantHeader.isBlank()) return;
        try {
            UUID tenantId = UUID.fromString(tenantHeader);
            setTenantId(tenantId);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid UUID format in X-Tenant-Id header: {}", tenantHeader);
        }
    }

    public static UUID getTenantId() {
        return (UUID) get(AppContextThreadLocalFilter.TENANT_HEADER);
    }

    public static void setXJwtAssertion(String wso2header) {
        if (wso2header != null && ! wso2header.isBlank()){
            put(AppContextThreadLocalFilter.X_JWT_ASSERTION, wso2header);
        }
    }

    public static String getXJwtAssertion(){
        return (String) get(AppContextThreadLocalFilter.X_JWT_ASSERTION);
    }

    public static final String ROLE_HEADER = "roles";

    public static void setRoles(List<String> roles){
        put(ROLE_HEADER,roles);
    }
    public static List<String> getRoles(){
        return (List<String>) get(ROLE_HEADER);
    }

    public static void setUsername(String username) {
        put(AppContextThreadLocalFilter.USERNAME,username);
    }
    public static String getUsername(){
        return (String) get(AppContextThreadLocalFilter.USERNAME);
    }
}
