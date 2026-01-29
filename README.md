# Graph Manager

Spring Boot MVC + Thymeleaf app for managing a simple directed graph stored in PostgreSQL.

## Requirements

- Java 17
- Maven 3.9+
- Docker (for local PostgreSQL)

## Running locally

1. Start PostgreSQL with Docker Compose:

```bash
docker compose up -d
```

2. Run database migrations and start the app:

```bash
mvn spring-boot:run
```

3. Open the UI:

```
http://localhost:8080/graph
```

## Configuration

Database settings live in `src/main/resources/application.yml` and default to:

- Database: `graphdb`
- User: `graph`
- Password: `graph`
- URL: `jdbc:postgresql://localhost:5432/graphdb`

## Notes

- Nodes can be created and deleted.
- Edges are directed and enforce:
  - no self-loops
  - no duplicate `(from, to)` pairs
