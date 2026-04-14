const { loginWithPasswordBasicAuth , loginWithPassword } = require('./axiosutil.js');
const { printAccessToken, isTokenExpired } = require('./util.js');

// 1. AGGIUNTO "async" per poter usare await all'interno
async function basicAuth(bru) { 
    const fullUrl = bru.getEnvVar("wso2_token_url");
    const username = bru.getEnvVar("wso2_admin_username");
    const password = bru.getEnvVar("wso2_admin_password");
    const clientId = bru.getEnvVar("wso2_client_id");
    const clientSecret = bru.getEnvVar("wso2_client_password"); 
    const scope = bru.getEnvVar("wso2_scopes");
    const adminToken = bru.getEnvVar("wso2_jwt");
    const adminTokenExpired = isTokenExpired(adminToken);
  

    //if (adminTokenExpired) {
    if(true){
        try {
          console.log('Using scopes ' + scope);
          const response = await loginWithPasswordBasicAuth(fullUrl, clientId, clientSecret, username, password, scope);
          //const response = await loginWithPassword(fullUrl, clientId, clientSecret, username, password, scope);
          const body = response.data;
          console.log('Wso2 Access Token ' + body);
          printAccessToken('Access Token', body.access_token);
          printAccessToken('Id Token', body.id_token);
          return {
                accessToken: body.access_token,
                idToken: body.id_token
          };
        } catch (error) {
            const errorMsg = error.response ? JSON.stringify(error.response.data) : error.message;
            console.error("❌ Fallimento autenticazione WSO2 Admin: ", errorMsg);
        }
    } else {
        console.log("⚡ Token WSO2 ancora valido. Bypass del login.");
    }
}

module.exports = {
    basicAuth
};