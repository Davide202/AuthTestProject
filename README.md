````markdown
# Ambiente di Sviluppo Locale: Keycloak, WSO2 e Spring Boot

Questo repository contiene l'infrastruttura Docker necessaria per avviare 
l'ambiente di sviluppo locale comprendente l'Identity Provider (Keycloak), 
l'API Manager (WSO2), il database PostgreSQL e i relativi backend Spring Boot.

## ⚠️ Prerequisito Fondamentale: Inizializzazione WSO2 (Solo la prima volta)

Poiché le cartelle di configurazione e database di WSO2 sono mappate localmente (Bind Mount) 
per permetterne il versionamento, **se avvii il compose con le cartelle vuote WSO2 crasherà**.

Prima di avviare il progetto per la primissima volta, 
esegui questi comandi nel terminale per estrarre i file base dall'immagine Docker 
nelle tue cartelle locali:

```bash
# 1. Crea un container temporaneo (senza avviarlo)
docker create --name temp-wso2 wso2/wso2am:4.3.0

# 2. Copia i file di base nelle tue cartelle locali
docker cp temp-wso2:/home/wso2carbon/wso2am-4.3.0/repository/database ./wso2_db
docker cp temp-wso2:/home/wso2carbon/wso2am-4.3.0/repository/deployment/server ./wso2_server

# 3. Rimuovi il container temporaneo
docker rm temp-wso2
````

*(Nota: la cartella `wso2_db/` dovrebbe essere inserita nel `.gitignore` per evitare di versionare i file binari del database H2).*

-----

## 🚀 Avvio dell'Ambiente

Per costruire le immagini dei backend e avviare tutta l'infrastruttura in background, esegui:

```bash
docker compose up -d --build
```

Il flag `--build` assicura che qualsiasi modifica fatta al codice sorgente dei backend Spring Boot venga compilata in una nuova immagine prima dell'avvio.

-----

## 🛑 Arresto e Pulizia

Per fermare i container mantenendo intatti i dati dei database:

```bash
docker compose down
```

**⚠️ Attenzione: Pulizia totale (Hard Reset)**
Se hai bisogno di piallare l'ambiente (es. per ricaricare da zero il realm di Keycloak o resettare il DB PostgreSQL), aggiungi il flag `-v`. Questo eliminerà i container e **distruggerà tutti i volumi Docker non locali**:

```bash
docker compose down -v
```

-----

## 🔗 Accesso ai Servizi e Credenziali

Una volta che tutti i container sono "healthy", puoi accedere ai servizi tramite i seguenti link.

### 🔐 Keycloak (Porta 8081)

- **Admin Console:** [http://localhost:8081/admin](https://www.google.com/search?q=http://localhost:8081/admin)
    - Username: `kcadmin`
    - Password: `kcadmin`
- **Realm pre-caricato:** `finmatica`
- **Utenti di test per le API:**
    - `admin@test.local` / `Admin123!` (Ruolo: ADMIN)
    - `user1@test.local` / `User123!` (Ruolo: USER)

### ⚙️ WSO2 API Manager (Porta 9443)

- **Management Console (Carbon):** [https://localhost:9443/carbon](https://www.google.com/search?q=https://localhost:9443/carbon)
- **Publisher Portal:** [https://localhost:9443/publisher](https://www.google.com/search?q=https://localhost:9443/publisher)
- **Developer Portal:** [https://localhost:9443/devportal](https://www.google.com/search?q=https://localhost:9443/devportal)
    - Username default: `admin`
    - Password default: `admin`

### 🐘 pgAdmin (Porta 5050)

- **Interfaccia Web:** [http://localhost:5050](https://www.google.com/search?q=http://localhost:5050)
    - Username: `admin@test.com`
    - Password: `admin`
      *(Nota: Il server PostgreSQL è esposto sulla porta `5433` dell'host).*

### ☕ Backend (Spring Boot)

- **Basic Auth Backend:** http://localhost:8082/basicAuth
- **Keycloak Backend:** http://localhost:8083/keycloak
    - *Swagger UI:* [http://localhost:8083/keycloak/swagger-ui/index.html](https://www.google.com/search?q=http://localhost:8083/keycloak/swagger-ui/index.html)
- **WSO2 Backend:** http://localhost:8084/wso2

<!-- end list -->


