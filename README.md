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

## Test scenario: Neo4j outage and Grafana alert

This is the main validation use case for the observability setup.

### Goal

Manually stop Neo4j, call the inventory API, and confirm that the application raises a clear error in logs and Grafana alerts on that failure.

### Steps

1. Start the stack:

```bash
docker compose up --build -d
```

2. Confirm the services are running:

```bash
docker compose ps
```

3. Stop Neo4j:

```bash
docker compose stop neo4j
```

4. Trigger a request that hits the database:

```bash
curl "http://localhost:8080/search-asset?assetName=test"
```

or

```bash
curl -X POST http://localhost:8080/create-asset \
  -H "Content-Type: application/json" \
  -d '{
    "assetName": "test-asset",
    "assetType": "APPLICATION",
    "environment": "DEV"
  }'
```

5. Open Grafana at `http://localhost:3001`.

6. Go to Explore and select the Loki datasource.

7. Run a query such as:

```logql
{application="usm-inventory"} |= "NEO4J_UNAVAILABLE"
```

You should see the Neo4j error log message in Loki.

### Alert rule

Use the following Loki query to create a Grafana alert:

```logql
count_over_time({application="usm-inventory"} |= "NEO4J_UNAVAILABLE" [5m]) > 0
```

Alert settings:

- Condition: `WHEN last() OF query(A) IS ABOVE 0`
- Evaluate every: `1m`
- For: `1m`

This means Grafana will trigger an alert whenever the application logs a Neo4j outage event in the last 5 minutes.

### Restart Neo4j

Once you finish testing, restart the database:

```bash
docker compose start neo4j
```

---

## Monitoring use cases to validate

The following scenarios are the recommended validation steps for this project.

### Use Case 1 — Log-based troubleshooting

Scenario:

- Service2 fails to connect to PostgreSQL.
- The developer searches application logs to identify the exception and root cause.

In this project, the equivalent test is a Neo4j outage.

Steps:

1. Start the stack:

```bash
docker compose up --build -d
```

2. Stop Neo4j:

```bash
docker compose stop neo4j
```

3. Trigger a DB-dependent request:

```bash
curl "http://localhost:8080/search-asset?assetName=test"
```

4. Open Grafana at `http://localhost:3001`.

5. Go to Explore → Loki and run:

```logql
{application="usm-inventory"} |= "NEO4J_UNAVAILABLE"
```

Expected result:

- The failed database request appears in logs.
- The log clearly identifies a Neo4j connection failure.

Tool used:

- Docker logs or Grafana Loki

---

### Use Case 2 — Metrics-based incident detection

Scenario:

- Service1 HTTP 500 error rate exceeds a threshold.
- The developer investigates Grafana dashboards to identify when the issue started and which service is affected.

Steps:

1. Start the stack:

```bash
docker compose up --build -d
```

2. Trigger application traffic:

```bash
curl "http://localhost:8081/usm-agent"
```

3. Open Grafana at `http://localhost:3001`.

4. Go to Explore → Prometheus and run:

```promql
sum(rate(http_server_requests_seconds_count{status=~"5.."}[5m]))
```

5. To view by service:

```promql
sum by (service) (rate(http_server_requests_seconds_count{status=~"5.."}[5m]))
```

Expected result:

- A spike in HTTP 5xx requests appears in the query output.
- The developer can identify when the incident started and which service was affected.

Tools used:

- Prometheus + Grafana

---

### Use Case 3 — Distributed-trace root-cause analysis

Scenario:

- Service1 returns errors.
- The developer discovers through a distributed trace that Service2 or its database call is the source of the failure.

In this project, the equivalent trace backend is Tempo, which is configured through Grafana.

Steps:

1. Start the stack:

```bash
docker compose up --build -d
```

2. Trigger a request through the service chain:

```bash
curl "http://localhost:8081/usm-agent"
```

3. Open Grafana at `http://localhost:3001`.

4. Go to Explore → Tempo.

5. Search traces by service or trace ID, then inspect the service flow.

Expected result:

- The trace shows the call chain between the agent and the inventory service.
- If the Neo4j call fails, the downstream error path is visible in the trace.

Tools used:

- OpenTelemetry + Tempo (Grafana trace view)

---

## Notes

- Neo4j credentials used in Docker are `neo4j / neo4j@123`.
- The app uses Docker networking internally, so the services can communicate with each other without manual change to the code.
- This project is designed to showcase microservice communication and observability patterns in a simple local environment.
- The Neo4j outage test is a realistic failure scenario for validating Loki-based Grafana alerting.
- For trace-based analysis, Tempo is used in this stack instead of Jaeger.
