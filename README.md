# Restful SQL Count Service

This Spring Boot service provides a single endpoint to count rows in a table or view using a safe, parameterized query.

## Run database with Docker

```bash
docker-compose up -d
```

The database is initialized with sample tables (`customers`, `orders`) and a view (`active_customers`).

## Run the app

```bash
./mvnw spring-boot:run
```

## Example request

```bash
curl -X POST http://localhost:8080/api/count \
  -H 'Content-Type: application/json' \
  -d '{
    "table": "orders",
    "conditions": [
      {"column": "status", "operator": "EQ", "value": "OPEN"}
    ]
  }'
```

Response:

```json
{"count":2}
```
