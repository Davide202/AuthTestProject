
mkdir logs
mkdir security

#  Entra nella cartella security
cd security

#  1. Genera il Keystore (wso2carbon.jks) in un'unica riga
keytool -genkeypair -alias wso2carbon -keyalg RSA -keysize 2048 -validity 3650 -keystore wso2carbon.jks -storepass wso2carbon -keypass wso2carbon -dname "CN=localhost, OU=API Management, O=WSO2Dev, L=Milan, ST=Lombardy, C=IT" -ext SAN=dns:localhost,ip:127.0.0.1 -storetype JKS

#  2. Esporta il certificato pubblico
keytool -exportcert -alias wso2carbon -keystore wso2carbon.jks -storepass wso2carbon -file wso2carbon.pem

#  3. Crea il Truststore (client-truststore.jks) importando il certificato
keytool -importcert -alias wso2carbon -keystore client-truststore.jks -storepass wso2carbon -file wso2carbon.pem -noprompt -storetype JKS

#  4. Rimuovi il file temporaneo (su Windows si usa del)
del wso2carbon.pem

#  Torna alla cartella principale
cd ..

#  Linux Only:
# Assegna i permessi corretti alla cartella
# sudo chown -R 802:802 security/