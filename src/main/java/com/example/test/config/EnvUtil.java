package com.example.test.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Environment util.
 *
 * Integer.valueOf(environment.getProperty("server.port"));
 *
 * Local address
 * InetAddress.getLocalHost().getHostAddress();
 * InetAddress.getLocalHost().getHostName();
 *
 * Remote address
 * InetAddress.getLoopbackAddress().getHostAddress();
 * InetAddress.getLoopbackAddress().getHostName();
 */
@Component
public class EnvUtil {
    @Autowired
    Environment environment;

    private String port;
    private String hostname;
    private String contextPath;

    public String getPort() {
        if (port == null)
            port = environment.getProperty("server.port");
        return port;
    }

    public Integer getPortAsInt() {
        return Integer.valueOf(getPort());
    }

    public String getHostname() {
        if (hostname == null){
            try{
                hostname = InetAddress.getLocalHost().getHostAddress();
            } catch (Exception e) {
                e.printStackTrace();
                hostname = InetAddress.getLoopbackAddress().getHostAddress();
            }
        }
        return "localhost";
    }

    public String getServerUrlPrefix() {
        return "http://" + getHostname() + ":" + getPort() + getContextPath();
    }

    private String getContextPath() {
        if (contextPath == null)
            contextPath = environment.getProperty("server.servlet.context-path");
        return contextPath;
    }
}
