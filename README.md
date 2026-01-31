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

3. Start the Unified Authentication Service (UAS) on port `8081` and configure JWT validation (see
   **Authentication & Security** below).

4. Open the admin UI:

- http://localhost:8080/admin/nodes
- http://localhost:8080/admin/edges
- http://localhost:8080/admin/phones
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

### Authentication & Security (JWT Resource Server)

This app is now a Spring Security resource server. It expects **access tokens** as Bearer JWTs issued
by the Unified Authentication Service (UAS) and stores them in HttpOnly cookies for browser flows.

**Required configuration**

Set the JWT issuer or JWKS endpoint provided by the UAS:

```yaml
auth:
  base-url: http://localhost:8081
security:
  jwt:
    issuer-uri: ${AUTH_ISSUER_URI:}
    jwk-set-uri: ${AUTH_JWK_SET_URI:}
```

If the UAS does **not** provide issuer/JWKS settings, you can **only for local development** supply
an HMAC secret:

```yaml
security:
  jwt:
    hmac-secret: ${AUTH_JWT_HMAC_SECRET:}
```

> ⚠️ Never use the HMAC option in production.

**Cookies**

The UI flow stores tokens as HttpOnly cookies:

- `ACCESS_TOKEN` (JWT access token, Max-Age uses `expiresInSeconds`)
- `REFRESH_TOKEN` (opaque refresh token, Max-Age uses `auth.refresh-cookie-max-age`)

Both cookies are `SameSite=Lax` and `HttpOnly`. Configure secure cookies in production:

```yaml
auth:
  cookie-secure: true
```

**Protected endpoints**

- Public:
  - `GET /public/graph`
  - `/login`, `/otp/**`, `/auth/refresh`, Swagger UI
- Authenticated:
  - `/admin/**`, `/graph/**`
  - `POST /public/**`, `PATCH /public/**`

**OTP login flow**

1. `GET /login` → submit phone number to `POST /otp/start`
2. `GET /otp/verify?challengeId=...` → submit OTP to `POST /otp/verify`
3. Tokens are set in HttpOnly cookies and you are redirected to `/admin/nodes`

**Refresh flow**

`POST /auth/refresh` reads the `REFRESH_TOKEN` cookie and updates both token cookies.

**Logout**

`POST /logout` sends the refresh token to the UAS `/api/v1/auth/logout` endpoint and clears cookies.

### Local dev escape hatch

If you need to disable auth entirely for local development, you can run with the `permit-all`
profile (this is isolated by profile and disabled by default):

```bash
SPRING_PROFILES_ACTIVE=permit-all mvn spring-boot:run
```

## Seed data

The database is seeded with a minimal public vocabulary category attached to a system node.

## Future entity ideas (not implemented)

- Authentication via phone OTP
- Privacy scopes
- Relationship types (mother/father/spouse) via edge label/value
- Import/export GEDCOM-like formats
- Search and merge duplicates
- Audit history for values (already supported by value versioning)
