# PRFlow Local PostgreSQL + Flyway Lifecycle

This document defines the local DB lifecycle for PRFlow and how to verify webhook persistence.

## 1) Docker Postgres Configuration

`infra/docker/docker-compose.yml` must define:

- `POSTGRES_DB=${POSTGRES_DB}`
- `POSTGRES_USER=${POSTGRES_USER}`
- `POSTGRES_PASSWORD=${POSTGRES_PASSWORD}`
- port mapping `${POSTGRES_PORT}:5432`

`infra/docker/.env` (from `.env.example`) should include:

```env
POSTGRES_DB=prflow_db
POSTGRES_USER=prflow_app
POSTGRES_PASSWORD=change_me_in_local_env
POSTGRES_PORT=5432
```

On **first** container initialization (fresh volume), Postgres auto-creates `POSTGRES_DB`.

## 2) Correct Connection Strings

Spring Boot (`application.yml` default):

```text
jdbc:postgresql://localhost:5432/prflow_db
```

Webhook service (`integrations/github-webhook-service/.env`):

```env
DATABASE_URL=postgresql://prflow_app:change_me_in_local_env@localhost:5432/prflow_db
```

## 3) DB Lifecycle (Authoritative Flow)

1. Postgres container starts.
2. Postgres initializes cluster and creates `prflow_db` (first boot only).
3. Spring Boot starts and Flyway runs migrations `V1..V4` inside `prflow_db`.
4. Tables are created (`organizations`, `developers`, `repositories`, `webhook_logs`, `flyway_schema_history`, ...).
5. Webhook service connects and inserts webhook rows into `webhook_logs`.

Flyway manages schema inside an existing database; it does not create the database server or database container.

## 4) Verification Commands

### List containers

```bash
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
```

### Inspect postgres container

```bash
docker inspect prflow-postgres --format '{{json .Config.Env}}'
docker logs --tail=100 prflow-postgres
```

### Connect to postgres container

```bash
docker exec -it prflow-postgres bash
```

### List databases

```bash
psql -U prflow_app -d postgres -c "\\l"
```

### Check Flyway metadata table

```bash
psql -U prflow_app -d prflow_db -c "SELECT version, description, success FROM flyway_schema_history ORDER BY installed_rank;"
```

### Verify webhook tables

```bash
psql -U prflow_app -d prflow_db -c "\\dt"
psql -U prflow_app -d prflow_db -c "SELECT COUNT(*) FROM webhook_logs;"
```

## 5) Local Operational Workflow

### One-time bootstrap

```bash
./scripts/setup-local.sh
```

### Run Flyway migrations (via Spring Boot)

```bash
cd backend/spring-api
./mvnw spring-boot:run
```

### Start webhook service

```bash
cd integrations/github-webhook-service
bun install
bun run dev
```

### Expected webhook success path

GitHub webhook -> webhook service -> PostgreSQL connection succeeds -> `webhook_logs` insert -> internal event publish continues.

## 6) If `database "prflow" does not exist` appears

It means some client is still using the wrong DB name (`prflow`). Replace it with `prflow_db` in env/config and restart that service.

## 7) If Docker fails with `failed to bind host port ... 5432 ... address already in use`

Another local process is already listening on port `5432` (often another Postgres instance).

### Diagnose

```bash
ss -ltnp '( sport = :5432 )'
# or
lsof -iTCP:5432 -sTCP:LISTEN -Pn
```

### Fix options

1. Stop the process using `5432`, then rerun setup:

```bash
docker compose --env-file infra/docker/.env -f infra/docker/docker-compose.yml down
./scripts/setup-local.sh
```

2. Or change Docker Postgres host port in `infra/docker/.env`:

```env
POSTGRES_PORT=5433
```

Then rerun setup (`DATABASE_URL` and Spring datasource URL are derived from this value).
