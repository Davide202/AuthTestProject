function printAccessToken(prefix, token) {
    let message = '';
    try {
        if(!token) return;
        message += ' HEADER :: ' + JSON.stringify(getJwtHeaderAsJSON(token));
        message += ' ,BODY :: ' + JSON.stringify(getJwtBodyAsJSON(token));
        /*
        let atp = token.split('.');
        if (atp.length > 1) {
            for (let i = 0; i < 2; i++) {
                let p = atp[i];
                let base64 = p.replace(/-/g, '+').replace(/_/g, '/');
                let pad = base64.length % 4;
                if (pad) {
                    base64 += '='.repeat(4 - pad);
                }
                let decoded = JSON.parse(atob(base64));
                message += JSON.stringify(decoded) + ' ;';
            }
        }
        */
    } catch (e) {
        console.error("Error parsing JWT:", e);
    }
    console.log(prefix + " :: [" + message + "]");
}

function getJwtHeaderAsJSON(token) {
    try {
        let atp = token.split('.');
        if (atp.length > 1) {
            let base64 = atp[0].replace(/-/g, '+').replace(/_/g, '/');
            let pad = base64.length % 4;
            if (pad) {
                base64 += '='.repeat(4 - pad);
            }
            return JSON.parse(atob(base64)); // <-- AGGIUNTO IL RETURN
        }
    } catch (e) {
        return {};
    }
    return {};
}

function getJwtBodyAsJSON(token) {
    try {
        let atp = token.split('.');
        if (atp.length > 1) {
            let base64 = atp[1].replace(/-/g, '+').replace(/_/g, '/');
            let pad = base64.length % 4;
            if (pad) {
                base64 += '='.repeat(4 - pad);
            }
            return JSON.parse(atob(base64)); // <-- AGGIUNTO IL RETURN
        }
    } catch (e) {
        return {};
    }
    return {};
}

// Piccola funzione per leggere la scadenza del JWT
function isTokenExpired(token) {
    if (!token) return true; // Se non c'è, è sicuramente "scaduto"
    try {
        // Usiamo la tua funzione helper!
        const decoded = getJwtBodyAsJSON(token); 
        
        if (!decoded.exp) return true; // Se non c'è l'exp, forziamo il refresh

        // Calcoliamo il timestamp attuale in secondi
        const now = Math.floor(Date.now() / 1000);
        // Se scade tra meno di 10 secondi, consideriamolo già da rinnovare
        return decoded.exp < (now + 10);
    } catch (e) {
        console.error("Error parsing JWT in isTokenExpired:", e);
        return true; 
    }
}

// Esporta la funzione per renderla visibile agli altri script
module.exports = {
    printAccessToken,
    isTokenExpired,
    getJwtHeaderAsJSON,
    getJwtBodyAsJSON
};