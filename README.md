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
- **UserEntity:** user identity linked to a node (BIGSERIAL id).
- **ProfileEntity:** versioned profile metadata mapped to UnifiedAuth external UUID.
- **NodeValue / EdgeValue / Profile:** versioned values with `created_at` / `expired_at`.
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

2. Run database migrations and start the app (JWT disabled by default):

```bash
mvn spring-boot:run
```

3. (Optional) Start the Unified Authentication Service (UAS) and enable JWT validation with the
   `local` profile (see **Authentication & Security** below).

4. Open the admin UI:

- http://localhost:8080/admin/nodes
- http://localhost:8080/admin/edges
- http://localhost:8080/admin/users
- http://localhost:8080/graph/view

## Reset database after migration squash

Migrations have been squashed into a single `V1__init.sql`. If you previously ran older
migrations, you must reset the database so Flyway can apply the new baseline from scratch.
This **deletes all existing data** (including the old demo family).

**Option A: drop and recreate the database (recommended)**

```sql
DROP DATABASE graphdb;
CREATE DATABASE graphdb;
```

Then start the app again so Flyway applies the new migration.

**Option B: drop and recreate the public schema**

```sql
DROP SCHEMA public CASCADE;
CREATE SCHEMA public;
```

Then start the app again so Flyway applies the new migration.

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
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -H "Accept: application/ld+json" \
  -d '{
    "nodes": [
      { "value": { "value": "New Person" } }
    ],
    "edges": [],
    "users": []
  }'
```

```bash
curl -X PATCH "http://localhost:8080/public/values" \
  -H "Authorization: Bearer <token>" \
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

### Authentication & Security (JWT Resource Server)

This app is a Spring Security resource server. It expects **access tokens** as Bearer JWTs issued
by the Unified Authentication Service (UAS). Rumor does **not** implement login, OTP, or refresh
flows. Configure the UAS as the only issuer and validate JWTs locally with JWKS.

**Required configuration (when enabled)**

Set the JWT issuer or JWKS endpoint provided by the UAS (discovery or direct JWKS):

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: ${AUTH_ISSUER_URI:}
          jwk-set-uri: ${AUTH_JWK_SET_URI:}

auth:
  security:
    enabled: false
  expected-issuer: ${AUTH_EXPECTED_ISSUER:}
  expected-audience: ${AUTH_EXPECTED_AUDIENCE:rumor}
  permit-public: true
```

`issuer-uri` uses OpenID discovery (`/.well-known/openid-configuration`); `jwk-set-uri` points
directly at the UAS JWKS endpoint (e.g. `/.well-known/jwks.json`).

**Expected claims**

- `roles`: list of roles, mapped to `ROLE_*`
- `scope` or `scp`: scopes, mapped to `SCOPE_*`
- `aud`: must include the expected audience (default `rumor`)

**Examples**

```bash
curl -H "Authorization: Bearer <token>" http://localhost:8080/api/me
```

**Protected endpoints**

- Public:
  - `GET /public/graph` (only when `auth.permit-public=true`)
  - `/admin/**` (temporary open)
- Authenticated:
  - `/api/**`
  - `POST /public/**`, `PATCH /public/**`
  - all other endpoints by default

### Local dev with UnifiedAuth

To enable JWT validation against a local UnifiedAuth instance, run with the `local` profile:

```bash
SPRING_PROFILES_ACTIVE=local mvn spring-boot:run
```

This points the issuer to `http://localhost:8080` and turns on `auth.security.enabled=true`.

### Local dev escape hatch

If you need to disable auth entirely for local development, you can run with the `permit-all`
profile (this is isolated by profile and disabled by default):

```bash
SPRING_PROFILES_ACTIVE=permit-all mvn spring-boot:run
```

## Seed data

The database is seeded with a minimal public vocabulary category attached to a system node.

## Future entity ideas (not implemented)

- Privacy scopes
- Relationship types (mother/father/spouse) via edge label/value
- Import/export GEDCOM-like formats
- Search and merge duplicates
- Audit history for values (already supported by value versioning)
