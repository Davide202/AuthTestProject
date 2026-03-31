const axios = require('axios');
const https = require('https'); // <-- 1. IMPORTIAMO IL MODULO HTTPS

// 2. CREIAMO UN AGENTE CHE IGNORA I CERTIFICATI NON VALIDI
const insecureAgent = new https.Agent({  
  rejectUnauthorized: false 
});
// Funzione Helper per ignorare parametri null/undefined
const toFormUrlEncoded = (obj) => {
    return Object.keys(obj)
        .filter(k => obj[k] !== undefined && obj[k] !== null)
        .map(k => encodeURIComponent(k) + '=' + encodeURIComponent(obj[k]))
        .join('&');
};

// ==========================================
// 1A. PASSWORD CREDENTIALS (Client Auth nel BODY)
// Uso: Client Pubblici o Identity Provider configurati per 'client_secret_post'
// ==========================================
async function loginWithPassword(fullUrl, clientId, clientSecret, username, password, scope) {
    const body = {
        grant_type: 'password',
        client_id: clientId,
        client_secret: clientSecret,
        username: username,
        password: password,
        scope: scope
    };

    const response = await axios.post(fullUrl, toFormUrlEncoded(body), {
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        httpsAgent: insecureAgent
    });
    return response;
}

// ==========================================
// 1B. PASSWORD CREDENTIALS (Client Auth nell'HEADER)
// Uso: Client Confidential (es. WSO2 di default) che richiedono 'client_secret_basic'
// ==========================================
async function loginWithPasswordBasicAuth(fullUrl, clientId, clientSecret, username, password, scope) {
    const body = {
        grant_type: 'password',
        username: username,
        password: password,
        scope: scope
    };

    // Creiamo la stringa Base64 per l'header Basic
    const credentials = `${clientId}:${clientSecret}`;
    const encodedCredentials = Buffer.from(credentials).toString('base64');

    const response = await axios.post(fullUrl, toFormUrlEncoded(body), {
        headers: { 
            'Content-Type': 'application/x-www-form-urlencoded',
            'Authorization': `Basic ${encodedCredentials}`
        },
        httpsAgent: insecureAgent
    });
    return response;
}

// ==========================================
// 2. CLIENT CREDENTIALS (Server-to-Server)
// ==========================================
async function loginWithClientCredentials(fullUrl, clientId, clientSecret, scope) {
    const body = {
        grant_type: 'client_credentials',
        client_id: clientId,
        client_secret: clientSecret,
        scope: scope
    };

    const response = await axios.post(fullUrl, toFormUrlEncoded(body), {
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        httpsAgent: insecureAgent
    });
    return response;
}

// ==========================================
// 3. AUTHORIZATION CODE (Scambio del 'code')
// ==========================================
async function exchangeAuthorizationCode(fullUrl, clientId, clientSecret, code, redirectUri) {
    const body = {
        grant_type: 'authorization_code',
        client_id: clientId,
        client_secret: clientSecret,
        code: code,
        redirect_uri: redirectUri
    };

    const response = await axios.post(fullUrl, toFormUrlEncoded(body), {
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        httpsAgent: insecureAgent
    });
    return response;
}

// ==========================================
// 4. REFRESH TOKEN (Rinnovo token scaduto)
// ==========================================
async function refreshAccessToken(fullUrl, clientId, clientSecret, refreshToken) {
    const body = {
        grant_type: 'refresh_token',
        client_id: clientId,
        client_secret: clientSecret,
        refresh_token: refreshToken
    };

    const response = await axios.post(fullUrl, toFormUrlEncoded(body), {
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        httpsAgent: insecureAgent
    });
    return response;
}

// Esportiamo tutto per Bruno!
module.exports = {
    loginWithPassword,
    loginWithPasswordBasicAuth,
    loginWithClientCredentials,
    exchangeAuthorizationCode,
    refreshAccessToken
};