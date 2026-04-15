package com.example.test.config.wso2;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;


@Component
@Profile("wso2")
public class Wso2RestTemplateConfig {

    private final RestTemplate restTemplate;
    private final Boolean useDefaultRestTemplate;

    private Wso2RestTemplateConfig(
            @Value("${app.wso2.use-default-rest-template}") Boolean useDefaultRestTemplate,
            RestTemplate restTemplate
    ) {
        this.useDefaultRestTemplate = useDefaultRestTemplate;
        this.restTemplate = restTemplate;
    }

    public RestTemplate getRestTemplate() {
        if (Boolean.TRUE.equals(useDefaultRestTemplate))
            return this.restTemplate;
        return this.restTemplateTrustAllCerts();
    }

    private RestTemplate restTemplateTrustAllCerts() {
        // 1. Creiamo un TrustManager che accetta tutto
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                }
        };


        try {
            // 2. Inizializziamo il contesto SSL
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // 3. Creiamo una Factory personalizzata che FORZA l'uso del client Java base
            //    e inietta il nostro contesto SSL bypassando eventuali librerie di Keycloak
            SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory() {
                @Override
                protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
                    if (connection instanceof HttpsURLConnection httpsConnection) {
                        httpsConnection.setSSLSocketFactory(sslContext.getSocketFactory());
                        httpsConnection.setHostnameVerifier((hostname, session) -> true);
                    }
                    super.prepareConnection(connection, httpMethod);
                }
            };

            // 4. Restituiamo il nostro RestTemplate "corazzato"
            return new RestTemplate(requestFactory);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException(e);
        }
    }
}