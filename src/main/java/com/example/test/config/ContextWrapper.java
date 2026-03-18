package com.example.test.config;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Holds the current tenant ID for the request context (ThreadLocal).
 */
public class ContextWrapper {

    private static final ThreadLocal<Map<String,Object>> currentTenant = new ThreadLocal<>();

    public static void put(String key, Object value) {
        Map<String,Object> map = currentTenant.get();
        if (map == null) {map = new HashMap<>();}
        map.put(key, value);
        currentTenant.set(map);
    }

    public static Object get(String key) {
        Map<String,Object> map = currentTenant.get();
        if (map == null) {map = new HashMap<>();}
        return  map.get(key);
    }

    public static void clear() {
        currentTenant.remove();
    }




    private static final String TENANT_HEADER = "tenant";

    public static void setTenantId(UUID tenantId) {
        put(TENANT_HEADER, tenantId);
    }

    public static UUID getTenantId() {
        return (UUID) get(TENANT_HEADER);
    }




}
