const { loginWithPassword,loginWithPasswordBasicAuth } = require('./axiosutil.js');
const { printAccessToken,isTokenExpired,getJwtBodyAsJSON } = require('./util.js');

// 2. Recupera le variabili (Usa nomi esatti della Collezione)
const realm = bru.getEnvVar("keycloak_realm");
const baseUrl = bru.getEnvVar("keycloak_base_url");
const adminEmail = bru.getEnvVar("admin_email");
const adminPassword = bru.getEnvVar("admin_password");
const userEmail = bru.getEnvVar("keycloak_user_email");
const userPassword = bru.getEnvVar("keycloak_user_password");
const clientId = bru.getEnvVar("keycloak_client_id");
const adminToken = bru.getVar("admin_token");
const userToken = bru.getVar("user_token");
const scope = bru.getEnvVar("keycloak_scope");

const fullUrl = `${baseUrl}/realms/${realm}/protocol/openid-connect/token`.replace(/([^:]\/)\/+/g, "$1");

var adminTokenExpired = isTokenExpired(adminToken);
var userTokenExpired = isTokenExpired(userToken);

async function login(){
      if(adminTokenExpired){
          console.log(`✅ Retrieving Admin Token`);
      try {
          const response = await loginWithPassword(fullUrl, clientId, null, adminEmail, adminPassword, scope);
          if (response.data.access_token) {
              bru.setVar("admin_token", response.data.access_token);
              console.log("🆕 Nuovo Access Token salvato correttamente.");
          }
          if(response.data.id_token){
              bru.setVar("admin_id_token", response.data.id_token);
              const payload = getJwtBodyAsJSON(response.data.id_token);
              if (payload.sub) {
                  bru.setVar("admin_kc_user_id", payload.sub);
                  console.log("🆕 admin_kc_user_id salvato correttamente.");
              }
          }
      } catch (error) {
          const errorMsg = error.response ? JSON.stringify(error.response.data) : error.message;
          console.error("❌ Fallimento autenticazione Admin: ", errorMsg);
      }
  }
  
  if (userTokenExpired) {
      console.log(`✅ Retrieving User Token`);
          try {
              const response = await loginWithPassword(fullUrl, clientId, null, userEmail, userPassword, scope);

              if (response.data.access_token) {
                  bru.setVar("user_token", response.data.access_token);
                  console.log("🆕 Nuovo Access Token salvato correttamente.");
              }
              if(response.data.id_token){
                    bru.setVar("user_id_token", response.data.id_token);
                    const payload = getJwtBodyAsJSON(response.data.id_token);
                    if (payload.sub) {
                        bru.setVar("user_kc_user_id", payload.sub);
                        console.log("🆕 admin_kc_user_id salvato correttamente.");
                    }
                }
          } catch (error) {
              const errorMsg = error.response ? JSON.stringify(error.response.data) : error.message;
              console.error("❌ Fallimento autenticazione User: ", errorMsg);
          }
    }  
}

module.exports = {
    login
};
