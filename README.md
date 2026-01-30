# Graph Manager

A graph-based family genealogy manager (Шежіре) for building родственные связи.

## Project mission

This app models family data as a directed graph:

- **Node = person**
- **Edge = one unified connection entity** used in three modes:
  1. **Category (PUBLIC):** `from_id = null`
  2. **Note (PRIVATE):** `to_id = null`
  3. **Relation:** both ends present (parent → child)

## Entities overview

- **NodeEntity:** a person node.
- **EdgeEntity:** a single edge type that represents categories, private notes, or relations
  (`isCategory`, `isNote`, `isRelation` helpers).
- **PhoneEntity / PhonePatternEntity:** phone numbers linked to nodes + patterns.
- **NodeValue / EdgeValue / PhoneValue:** versioned values with `created_at` / `expired_at`.

## Example modeling patterns

**1) Gender category**

```
NULL -> [Category: Male] -> Ayan
```

**2) Private note**

```
Ayan -> [Note: "Loves hiking"] -> NULL
```

**3) Family relation**

```
Ayan    -> [Relation] -> Dias   (Ayan is parent of Dias)
Aigerim -> [Relation] -> Dias   (second incoming edge => second parent)
```

Two incoming relation edges mean two parents (мама/папа).

## Running locally

1. Start PostgreSQL with Docker Compose:

```bash
docker compose up -d
```

2. Run database migrations and start the app:

```bash
mvn spring-boot:run
```

3. Open the admin UI:

- http://localhost:8080/admin/nodes
- http://localhost:8080/admin/edges
- http://localhost:8080/admin/phones
- http://localhost:8080/graph/view

## Public API documentation

Swagger UI is available at:

- http://localhost:8080/swagger-ui/index.html

Example requests:

```bash
curl -H "Accept: application/ld+json" \
  "http://localhost:8080/public/graph?at=2026-01-30T10:00:00Z"
```

```bash
curl -X POST "http://localhost:8080/public/graph" \
  -H "Content-Type: application/json" \
  -H "Accept: application/ld+json" \
  -d '{
    "nodes": [
      { "value": { "value": "New Person" } }
    ],
    "edges": [],
    "phones": []
  }'
```

```bash
curl -X PATCH "http://localhost:8080/public/values" \
  -H "Content-Type: application/json" \
  -H "Accept: application/ld+json" \
  -d '{
    "edgeValue": {
      "edgeId": 1,
      "relationType": "PARENT"
    }
  }'
```

## Configuration

Database settings live in `src/main/resources/application.yml` and default to:

- Database: `graphdb`
- User: `graph`
- Password: `graph`
- URL: `jdbc:postgresql://localhost:5432/graphdb`

## Seed data

There is a mockup migration (`V10__mockup_demo_family.sql`) intended for training/new users.

## Future entity ideas (not implemented)

- Authentication via phone OTP
- Privacy scopes
- Relationship types (mother/father/spouse) via edge label/value
- Import/export GEDCOM-like formats
- Search and merge duplicates
- Audit history for values (already supported by value versioning)
