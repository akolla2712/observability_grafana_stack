# USM Observability Demo

This project is a small microservices-based sample that demonstrates asset management together with observability tooling. It contains two Spring Boot services and a supporting monitoring stack.

## What this application does

### 1. usm-inventory

The inventory service is the main data layer. It exposes REST APIs to:

- create an asset
- search an asset by name
- store asset metadata in Neo4j

Key endpoints:

- `POST /create-asset`
- `GET /search-asset?assetName=<name>`

This service is the backend that owns the asset records.

### 2. usm-agent

The agent service simulates asset processing and calls the inventory service APIs. It generates asset names and sends them to the inventory service for creation or lookup. This helps demonstrate how a producer/consumer-style service interacts with the backend and how traces/logs are emitted through the observability pipeline.

### 3. Observability stack

The project also starts:

- Neo4j for persistence
- Prometheus for metrics collection
- Loki for logs
- Tempo for tracing
- Grafana for visualization

Together, these tools help show how the application behaves while the services communicate with each other.

---

## Prerequisites

Before running the application, make sure you have:

- Docker Desktop or Docker Engine
- Docker Compose
- Java 21 (for local Maven runs)
- Maven

---

## Run the full application with Docker Compose

From the project root, run:

```bash
docker compose up --build -d
```

This command will:

- build the custom Docker images for both Spring Boot services
- start Neo4j, Prometheus, Grafana, Loki, Tempo, and the app services
- run the application in the background

### Access points

After startup, you can open:

- `http://localhost:8080` -> usm-inventory
- `http://localhost:8081` -> usm-agent
- `http://localhost:7474` -> Neo4j Browser
- `http://localhost:9090` -> Prometheus
- `http://localhost:3001` -> Grafana

### Check status

```bash
docker compose ps
```

### View logs

```bash
docker compose logs -f
```

### Stop the application

```bash
docker compose down
```

To remove volumes as well:

```bash
docker compose down -v
```

---

## Run the application locally with Maven

If you want to run each service on your machine without Docker:

### Inventory service

```bash
cd usm-invnetory
mvn spring-boot:run
```

### Agent service

```bash
cd usm-agent
mvn spring-boot:run
```

---

## Example API usage

Create an asset:

```bash
curl -X POST http://localhost:8080/create-asset \
  -H "Content-Type: application/json" \
  -d '{
    "assetName": "Alpha-Asset-100",
    "assetType": "APPLICATION",
    "environment": "DEV"
  }'
```

Search an asset:

```bash
curl "http://localhost:8080/search-asset?assetName=Alpha-Asset-100"
```

---

## Notes

- Neo4j credentials used in Docker are `neo4j / neo4j@123`.
- The app uses Docker networking internally, so the services can communicate with each other without manual change to the code.
- This project is designed to showcase microservice communication and observability patterns in a simple local environment.
