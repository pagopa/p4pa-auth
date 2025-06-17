#  p4pa-auth

This application belong to the **inbound/outbound** tier of the **Piattaforma Unitaria** product.

See [PU Microservice Architecture](https://pagopa.atlassian.net/wiki/spaces/SPAC/pages/1405845916/Architettura+microservizi) for more details.

## 🧱 Role

* To handle users' sessions;
* To handle m2m clients;
* To store users and roles.

## 🌐 APIs
See [OpenAPI](openapi/p4pa-auth.openapi.yaml), exposed through the following path:
* `/swagger-ui/index.html`

See [Postman collection](/postman/p4pa-auth-E2E.postman_collection.json) and [Postman Environment](https://pagopa.atlassian.net/wiki/spaces/SPAC/pages/1094615081/Environment+collection+postman).


### 📌 Relevant APIs
* `GET /payhub/.well-known/jwks.json`: JWKS containing public keys;
* `POST /payhub/oauth/token`: To obtain an access token;
* `GET /payhub/oauth/userinfo`: To validate the token and get logged user info;
* `POST /payhub/oauth/revoke`: To revoke an access token;
* `POST /payhub/oauth/clients/{organizationIpaCode}`: To register an organization client to use in M2M authentication;
* `DELETE /payhub/oauth/clients/{organizationIpaCode}/{clientId}`: To delete an organization client;
* `DELETE /actuator/caches/ACCESS_TOKEN`: To revoke all access tokens.

## 🔎 Monitoring
See available actuator endpoints through the following path:
* `/actuator`

### 📌 Relevant endpoints
* Health (provide an accessToken to see details): `/actuator/health`
  * Liveness: `/actuator/health/liveness`
  * Readiness: `/actuator/health/readiness`
* Metrics: `/actuator/metrics`
  * Prometheus: `/actuator/prometheus`

Further endpoints are exposed through the JMX console.

## ✏️ Logging
See [log configured pattern](/src/main/resources/logback-spring.xml).

## 🔗 Dependencies

### 🗄️ Resources
* Redis
* MongoDB

### 🧩 Microservices
* [p4pa-organization](https://github.com/pagopa/p4pa-organization): To retrieve organization info and add them to the user info.

### 🌍 External
* External IAM - The IAM which will provide the id token used to authenticate users:
  * [JWKS endpoint](https://auth0.com/docs/secure/tokens/json-web-tokens/json-web-key-sets): Key sets to validate JWT signs.

## 🗃️ Entities handled
* `users`: Users authenticated through the external IAM;
* `operators`: Users' role inside an organization;
* `clients`: Organizations' clients for M2M authentication.

## 🔧 Configuration

See [application.yml](src/main/resources/application.yml) for each configurable property.

### 📌 Relevant configurations

#### 🌐 Application Server
| ENV         | DESCRIPTION                       | DEFAULT |
|-------------|-----------------------------------|---------|
| SERVER_PORT | Application server listening port | 8080    |

#### ✏️ Logging
| ENV                                   | DESCRIPTION                                                                                                                                                                     | DEFAULT |
|---------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------|
| LOG_LEVEL_ROOT                        | Base level                                                                                                                                                                      | INFO    |
| LOG_LEVEL_PAGOPA                      | Base level of custom classes                                                                                                                                                    | INFO    |
| LOG_LEVEL_SPRING                      | Level applied to Spring framework                                                                                                                                               | INFO    |
| LOG_LEVEL_SPRING_BOOT_AVAILABILITY    | To print availability events                                                                                                                                                    | DEBUG   |
| LOGGING_LEVEL_API_REQUEST_EXCEPTION   | Level applied to APIs exception                                                                                                                                                 | INFO    |
| LOG_LEVEL_PERFORMANCE_LOG             | Level applied to [PerformanceLog](https://pagopa.atlassian.net/wiki/spaces/SPAC/pages/1540096383/Logging#2.2.-Log-di-performance)                                               | INFO    |
| LOG_LEVEL_PERFORMANCE_LOG_API_REQUEST | Level applied to [API Performance Log](https://pagopa.atlassian.net/wiki/spaces/SPAC/pages/1540096383/Logging#2.2.2.1.-Log-di-perfomance-per-le-API)                            | INFO    |
| LOG_LEVEL_PERFORMANCE_LOG_REST_INVOKE | Level applied to [REST invoke Performance Log](https://pagopa.atlassian.net/wiki/spaces/SPAC/pages/1540096383/Logging#2.2.2.2.-Log-di-performance-per-i-servizi-REST-integrati) | INFO    |

#### 🔁 Integrations

##### 🗄️ Resources
| ENV                                           | DESCRIPTION                             | DEFAULT                   |
|-----------------------------------------------|-----------------------------------------|---------------------------|
| REDIS_HOST                                    | Redis server host                       | localhost                 |
| REDIS_PORT                                    | Redis server port                       | 6380                      |
| REDIS_PASSWORD                                | Redis password                          |                           |
| MONGODB_URI                                   | Mongo connection string                 | mongodb://localhost:27017 |
| MONGODB_DBNAME                                | Mongo db name                           | payhub                    |
| MONGODB_CONNECTIONPOOL_MAX_SIZE               | Mongo connection pool max size          | 100                       |
| MONGODB_CONNECTIONPOOL_MIN_SIZE               | Mongo connection pool max size          | 0                         |
| MONGODB_CONNECTIONPOOL_MAX_WAIT_MS            | Timeout milliseconds                    | 120000                    |
| MONGODB_CONNECTIONPOOL_MAX_CONNECTION_LIFE_MS | Connection lifetime (milliseconds)      | 0                         |
| MONGODB_CONNECTIONPOOL_MAX_CONNECTION_IDLE_MS | Connection idle lifetime (milliseconds) | 120000                    |
| MONGODB_CONNECTIONPOOL_MAX_CONNECTING         | Max parallel creating connections       | 2                         |

##### 📋 [Caching](https://pagopa.atlassian.net/wiki/spaces/SPAC/pages/1542128077/Caching)
| ENV                        | DESCRIPTION                                 | DEFAULT |
|----------------------------|---------------------------------------------|---------|
| CACHE_JWKS_SIZE            | External IAM jwks cache size                | 10      |
| CACHE_JWKS_MINUTES         | External IAM jwks cache retention (minutes) | 60      |
| CACHE_ORGANIZATION_SIZE    | Organization data cache size                | 100     |
| CACHE_ORGANIZATION_MINUTES | Organization data cache retention (minutes) | 60      |
| CACHE_BROKER_MAXIMUM_SIZE  | Broker data cache size                      | 100     |
| CACHE_BROKER_MINUTES       | Broker data cache retention (minutes)       | 60      |

##### 🔗 REST
| ENV                                               | DESCRIPTION                               | DEFAULT |
|---------------------------------------------------|-------------------------------------------|---------|
| DEFAULT_REST_CONNECTION_POOL_SIZE                 | Default connection pool size              | 10      |
| DEFAULT_REST_CONNECTION_POOL_SIZE_PER_ROUTE       | Default connection pool size per route    | 5       |
| DEFAULT_REST_CONNECTION_POOL_TIME_TO_LIVE_MINUTES | Default connection pool TTL (minutes)     | 10      |
| DEFAULT_REST_TIMEOUT_CONNECT_MILLIS               | Default connection timeout (milliseconds) | 120000  |
| DEFAULT_REST_TIMEOUT_READ_MILLIS                  | Default read timeout (milliseconds)       | 120000  |

##### 🧩 Microservices
| ENV                                | DESCRIPTION                                    | DEFAULT |
|------------------------------------|------------------------------------------------|---------|
| ORGANIZATION_BASE_URL              | Organization microservice URL                  |         |
| ORGANIZATION_MAX_ATTEMPTS          | Organization API max attempts                  | 3       |
| ORGANIZATION_WAIT_TIME_MILLIS      | Organization retry waiting time (milliseconds) | 500     |
| ORGANIZATION_PRINT_BODY_WHEN_ERROR | To print body when an error occurs             | true    |

##### 🌍 External services
| ENV                         | DESCRIPTION                                                                                               | DEFAULT                              |
|-----------------------------|-----------------------------------------------------------------------------------------------------------|--------------------------------------|
| JWT_EXTERNAL_TOKEN_BASE_URL | External IAM base URL (it will be used to build the jwks url appending the path `/.well-known/jwks.json`) | https://auth.server.com              |
| JWT_EXTERNAL_TOKEN_ISS      | External IAM issuer claim value                                                                           | externalauthentication-server-issuer |

#### 💼 Business logic
| ENV                              | DESCRIPTION                                                                                                                                                                                                                                                            | DEFAULT              |
|----------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------|
| JWT_TOKEN_AUDIENCE               | The aud claim set on the generated access token                                                                                                                                                                                                                        | application-audience |
| JWT_TOKEN_EXPIRATION_SECONDS     | Access token expiration (seconds)                                                                                                                                                                                                                                      | 3600                 |
| JWT_TOKEN_PRIVATE_KEY            | JWT private key                                                                                                                                                                                                                                                        |                      |
| JWT_TOKEN_PUBLIC_KEY             | JWT public key                                                                                                                                                                                                                                                         |                      |
| ACCESS_ORGANIZATION_MODE_ENABLED | If true, it will expect the presence of the access organization inside the ID Token. Thus, it will register te relation between the operator and the relation with the provided roles. If disabled, the admin should register the associations using the provided API. | true                 |

#### 🔑 keys
| ENV                                | DESCRIPTION                                                              | DEFAULT |
|------------------------------------|--------------------------------------------------------------------------|---------|
| DATA_CIPHER_P4PA_AUTH_HASH_KEY     | Base64 encoded key (256 bit) used to calculate hash                      |         |
| DATA_CIPHER_P4PA_AUTH_ENCRYPT_PSW  | Base64 encoded key (256 bit) used to encrypt data                        |         |
| PIATTAFORMA_UNITARIA_CLIENT_SECRET | client_secret used on M2M authentication to get a technical access token |         |

## 🛠️ Getting Started

### 📝 Prerequisites

Ensure the following tools are installed on your machine:

1. **Java 21+**
2. **Gradle** (or use the Gradle wrapper included in the repository)
3. **Docker** (to build and run on an isolated environment, optional)

### 🔐 Write Locks

```sh
./gradlew dependencies --write-locks
```

### ⚙️ Build

```sh
./gradlew clean build
```

### 🧪 Test

#### 📌 JUnit
```sh
./gradlew test
```

#### 📌 Postman
See [Postman collection](/postman/p4pa-auth-E2E.postman_collection.json) and [Postman Environment](https://pagopa.atlassian.net/wiki/spaces/SPAC/pages/1094615081/Environment+collection+postman).

### 🚀 Run local

```sh
./gradlew bootRun
```

### 🐳 Build & run through Docker
```sh
docker build -t <APP_NAME> .
docker run --env-file <ENV_FILE> <APP_NAME>
```