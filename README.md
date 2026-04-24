# Distributed NoSQL Database

A lightweight distributed NoSQL database built with Spring Boot, plus a Gmail-like demo app that uses the database as its storage backend.

## Project Overview

This repository contains five services orchestrated with Docker Compose:

- **bootstrap**: Registration and worker assignment service (round-robin load balancing).  
- **worker1** and **worker2**: Core NoSQL database workers with auth, CRUD, schema validation, replication, and recovery logic.  
- **backend** (`gmail-demo/backend`): Demo email API that persists and queries emails through the distributed workers.  
- **frontend** (`gmail-demo/frontend`): React UI for registering users, sending email, inbox/sent views, and reading/deleting messages.

## Architecture

```text
Frontend (React :3000)
        |
        v
Backend API (Spring :8085)
        |
        v
Bootstrap (Spring :8080)  ---> assigns user token + worker
        |
        v
Worker1 (:8081) <----replication/recovery----> Worker2 (:8082)
```

### How user routing works

1. A user registers through bootstrap (`POST /api/register/{username}`).
2. Bootstrap assigns a worker using round-robin and returns a token + worker URL.
3. The backend decodes worker affinity from the token and sends subsequent NoSQL operations to the assigned worker.
4. Workers also coordinate internal replication/recovery via `/internal/*` endpoints.

## Tech Stack

- **Java 17**
- **Spring Boot 4.0.5** (bootstrap, workers, backend)
- **React 19** + `react-scripts` (frontend)
- **Docker / Docker Compose**

## Repository Structure

```text
.
├── bootstrap/            # worker registration + token generation + round-robin LB
├── worker/               # distributed NoSQL worker service (run as worker1/worker2)
├── gmail-demo/
│   ├── backend/          # demo REST API that uses the distributed DB
│   └── frontend/         # React UI
├── docker-compose.yml
└── README.md
```

## Prerequisites

- Docker + Docker Compose
- Java 17
- Maven (or use the included Maven wrappers)
- Node.js 18+ (only needed if you run frontend outside Docker)

## Quick Start (Docker)

> The Java Dockerfiles copy `target/*.jar`, so build each Spring Boot app first.

### 1) Build JARs

```bash
cd bootstrap && ./mvnw clean package -DskipTests
cd ../worker && ./mvnw clean package -DskipTests
cd ../gmail-demo/backend && ./mvnw clean package -DskipTests
cd ../..
```

### 2) Start everything

```bash
docker compose up --build
```

### 3) Access services

- Frontend: `http://localhost:3000`
- Gmail demo backend: `http://localhost:8085`
- Bootstrap: `http://localhost:8080`
- Worker 1: `http://localhost:8081`
- Worker 2: `http://localhost:8082`

### 4) Stop

```bash
docker compose down
```

## Configuration Notes

### Worker identity and ports

- `worker1` runs with `SPRING_CONFIG_NAME=application-worker1` on port `8081`.
- `worker2` runs with `SPRING_CONFIG_NAME=application-worker2` on port `8082`.

### Default `application*.properties` (current repo values)

`worker/src/main/resources/application-worker1.properties`

```properties
spring.application.name=NoSQL_Database_Management_System
server.port=8081
node.name=worker1
node.workerPath = http://worker1:8081
bootstrap.url=http://bootstrap:8080
admin.username=admin
admin.token=admin123
```

`worker/src/main/resources/application-worker2.properties`

```properties
spring.application.name=NoSQL_Database_Management_System
server.port=8082
node.name=worker2
node.workerPath = http://worker2:8082
bootstrap.url=http://bootstrap:8080
admin.username=admin
admin.token=admin123
```

`gmail-demo/backend/src/main/resources/application.properties`

```properties
spring.application.name=gmail-demo
server.port=8085
db.bootstrap.url=http://bootstrap:8080
db.name=emaildb
```

`bootstrap/src/main/resources/application.properties`

```properties
spring.application.name=bootstrap
server.port=8080
bootstrap.url=http://bootstrap:8080
```

### Persistence

Docker volumes map local folders for worker data:

- `./databases_worker1 -> /app/databases_worker1`
- `./databases_worker2 -> /app/databases_worker2`

### Admin credentials (worker API)

Default admin headers in worker configs:

- `username: admin`
- `token: admin123`

These are required for privileged operations (e.g., creating/deleting databases and collections).

## API Highlights

## Bootstrap API

- `POST /api/register/{username}`  
  Returns assigned `token` and `worker` URL.

## Worker API (`/api`)

### Database operations

- `POST /createDB/{db}?replicated=false` (admin)
- `DELETE /deleteDB/{db}?replicated=false` (admin)
- `GET /getDBs`

### Collection operations

- `POST /createCol/{db}/{col}?replicated=false` (admin, body = schema)
- `DELETE /deleteCol/{db}/{col}?replicated=false` (admin)

### Document operations

- `POST /insertOne/{db}/{col}`
- `GET /getAllDocs/{db}/{col}`
- `GET /getDoc/{db}/{col}/{docId}`
- `PUT /updateDoc/{db}/{col}/{docId}/{field}/{value}/{version}`
- `DELETE /deleteDoc/{db}/{col}/{docId}`
- `GET /filter/{db}/{col}/{field}/{value}`

> Non-internal worker endpoints require `username` and `token` headers.

## Gmail Demo Backend API

- `POST /api/auth/register/{username}`
- `POST /api/emails/send`
- `GET /api/emails/inbox`
- `GET /api/emails/sent`
- `GET /api/emails/{id}`
- `DELETE /api/emails/{id}`

`/api/emails/*` expects headers:

- `username: <current user>`
- `token: <registration token>`

## Example Flow (cURL)

### Register a user

```bash
curl -X POST "http://localhost:8080/api/register/alice"
```

### Create a database and collection (admin)

```bash
curl -X POST "http://localhost:8081/api/createDB/emaildb" \
  -H "username: admin" \
  -H "token: admin123"

curl -X POST "http://localhost:8081/api/createCol/emaildb/emails" \
  -H "Content-Type: application/json" \
  -H "username: admin" \
  -H "token: admin123" \
  -d '{"fields":[{"name":"from","type":"STRING"},{"name":"to","type":"STRING"},{"name":"subject","type":"STRING"},{"name":"body","type":"STRING"},{"name":"timestamp","type":"INTEGER"},{"name":"read","type":"BOOLEAN"}]}'
```

### Insert a document

```bash
curl -X POST "http://localhost:8081/api/insertOne/emaildb/emails" \
  -H "Content-Type: application/json" \
  -H "username: alice" \
  -H "token: <alice_token>" \
  -d '{"from":"alice","to":"bob","subject":"Hello","body":"Hi Bob","timestamp":1710000000,"read":false}'
```

## Development Tips

- Start by reading `docker-compose.yml` to understand service wiring and ports.
- If Docker startup fails with missing JAR errors, ensure each Spring module was packaged first.
- If requests return `Wrong worker` or `Unauthorized`, verify token/user headers and send requests to the correct assigned worker.

