package com.example.test.context;

import java.util.Collections;
import java.util.List;

public class RequestContextDataBuilder {

    private String username;
    private String tenantId;
    private List<String> roles;
    private String traceId;
    private String wso2header;

    private RequestContextDataBuilder (){
        this.username = "";
        this.tenantId = "";
        this.roles = Collections.emptyList();
        this.traceId = "";
        this.wso2header = "";
    }

    public static RequestContextDataBuilder builder(){
        return new RequestContextDataBuilder();
    }

    public RequestContextDataBuilder username(String username){
        this.username = username;
        return this;
    }

    public RequestContextDataBuilder tenantId(String tenantId){
        this.tenantId = tenantId;
        return this;
    }
    public RequestContextDataBuilder roles(List<String> roles){
        this.roles = roles;
        return this;
    }
    public RequestContextDataBuilder traceId(String traceId){
        this.traceId = traceId;
        return this;
    }

    public RequestContextDataBuilder wso2header(String wso2header){
        this.wso2header = wso2header;
        return this;
    }

    public RequestContextData build(){
        return new RequestContextData(
                this.username,
                this.tenantId,
                this.roles,
                this.traceId,
                this.wso2header
        );
    }
}
