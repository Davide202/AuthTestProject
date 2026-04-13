package com.example.test.context;

import java.util.List;

public record RequestContextData(
        String username,
        String tenantId,
        List<String> roles,
        String traceId,
        String wso2header
) {

    public static RequestContextDataBuilder builder(){
        return RequestContextDataBuilder.builder();
    }



}
