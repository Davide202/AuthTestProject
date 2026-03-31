package com.example.test.config.wso2;


import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;


@Log4j2
//@Configuration
@Profile("wso2")
@Deprecated
public class Wso2SSLConfig {

    @PostConstruct
    public void disableSslVerification() {
        try {
            // Creiamo un TrustManager che accetta letteralmente qualsiasi certificato
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() { return null; }
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                    }
            };

            // Inizializziamo il contesto SSL con il nostro TrustManager "cieco"
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());

            // Applichiamo il contesto SSL ignorando anche i controlli sull'hostname
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);

            log.warn("⚠️ ATTENZIONE: Controllo SSL disabilitato globalmente (Solo per profilo WSO2 locale)");

        } catch (Exception e) {
            log.error(e);
        }
    }
}
