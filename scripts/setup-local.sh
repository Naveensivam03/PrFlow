#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
DOCKER_ENV_EXAMPLE="$ROOT_DIR/infra/docker/.env.example"
DOCKER_ENV="$ROOT_DIR/infra/docker/.env"
DOCKER_COMPOSE_FILE="$ROOT_DIR/infra/docker/docker-compose.yml"
WEBHOOK_ENV_EXAMPLE="$ROOT_DIR/integrations/github-webhook-service/.env.example"
WEBHOOK_ENV="$ROOT_DIR/integrations/github-webhook-service/.env"

require_cmd() {
  if ! command -v "$1" >/dev/null 2>&1; then
    echo "[ERROR] Missing required command: $1"
    exit 1
  fi
}

require_cmd docker
require_cmd sed

if [[ ! -f "$DOCKER_ENV_EXAMPLE" ]]; then
  echo "[ERROR] Missing $DOCKER_ENV_EXAMPLE"
  exit 1
fi

if [[ ! -f "$WEBHOOK_ENV_EXAMPLE" ]]; then
  echo "[ERROR] Missing $WEBHOOK_ENV_EXAMPLE"
  exit 1
fi

if [[ ! -f "$DOCKER_ENV" ]]; then
  cp "$DOCKER_ENV_EXAMPLE" "$DOCKER_ENV"
  echo "[INFO] Created $DOCKER_ENV from example"
else
  echo "[INFO] Using existing $DOCKER_ENV"
fi

if [[ ! -f "$WEBHOOK_ENV" ]]; then
  cp "$WEBHOOK_ENV_EXAMPLE" "$WEBHOOK_ENV"
  echo "[INFO] Created $WEBHOOK_ENV from example"
else
  echo "[INFO] Using existing $WEBHOOK_ENV"
fi

set -a
# shellcheck disable=SC1090
source "$DOCKER_ENV"
set +a

: "${POSTGRES_DB:?POSTGRES_DB is required in infra/docker/.env}"
: "${POSTGRES_USER:?POSTGRES_USER is required in infra/docker/.env}"
: "${POSTGRES_PASSWORD:?POSTGRES_PASSWORD is required in infra/docker/.env}"
: "${POSTGRES_PORT:?POSTGRES_PORT is required in infra/docker/.env}"

DATABASE_URL="postgresql://${POSTGRES_USER}:${POSTGRES_PASSWORD}@localhost:${POSTGRES_PORT}/${POSTGRES_DB}"

is_port_in_use() {
  local port="$1"
  if command -v ss >/dev/null 2>&1; then
    ss -ltn "( sport = :$port )" | tail -n +2 | grep -q .
    return
  fi
  if command -v lsof >/dev/null 2>&1; then
    lsof -iTCP:"$port" -sTCP:LISTEN -Pn >/dev/null 2>&1
    return
  fi
  return 1
}

upsert_env_var() {
  local file="$1"
  local key="$2"
  local value="$3"

  if grep -qE "^${key}=" "$file"; then
    sed -i "s|^${key}=.*|${key}=${value}|" "$file"
  else
    printf '\n%s=%s\n' "$key" "$value" >> "$file"
  fi
}

if grep -qE '^GITHUB_WEBHOOK_SECRET=replace' "$WEBHOOK_ENV" || grep -qE '^GITHUB_WEBHOOK_SECRET=$' "$WEBHOOK_ENV"; then
  if command -v openssl >/dev/null 2>&1; then
    WEBHOOK_SECRET="$(openssl rand -hex 32)"
    upsert_env_var "$WEBHOOK_ENV" "GITHUB_WEBHOOK_SECRET" "$WEBHOOK_SECRET"
    echo "[INFO] Generated GITHUB_WEBHOOK_SECRET in webhook .env"
  else
    echo "[WARN] openssl not found; please set GITHUB_WEBHOOK_SECRET manually in $WEBHOOK_ENV"
  fi
fi

upsert_env_var "$WEBHOOK_ENV" "PORT" "3001"
upsert_env_var "$WEBHOOK_ENV" "NODE_ENV" "development"
upsert_env_var "$WEBHOOK_ENV" "DATABASE_URL" "$DATABASE_URL"

if is_port_in_use "$POSTGRES_PORT"; then
  echo "[ERROR] Host port ${POSTGRES_PORT} is already in use."
  echo "[ERROR] Either stop the process using this port or change POSTGRES_PORT in $DOCKER_ENV."
  echo "[ERROR] Debug: ss -ltnp '( sport = :${POSTGRES_PORT} )' || lsof -iTCP:${POSTGRES_PORT} -sTCP:LISTEN -Pn"
  exit 1
fi

echo "[INFO] Starting docker services..."
docker compose --env-file "$DOCKER_ENV" -f "$DOCKER_COMPOSE_FILE" up -d --force-recreate postgres valkey

POSTGRES_CONTAINER="prflow-postgres"

echo "[INFO] Waiting for postgres healthcheck..."
for _ in {1..30}; do
  status="$(docker inspect -f '{{if .State.Health}}{{.State.Health.Status}}{{else}}unknown{{end}}' "$POSTGRES_CONTAINER" 2>/dev/null || true)"
  if [[ "$status" == "healthy" ]]; then
    echo "[INFO] Postgres is healthy"
    break
  fi
  sleep 2
done

status="$(docker inspect -f '{{if .State.Health}}{{.State.Health.Status}}{{else}}unknown{{end}}' "$POSTGRES_CONTAINER" 2>/dev/null || true)"
if [[ "$status" != "healthy" ]]; then
  echo "[ERROR] Postgres did not become healthy. Current status: $status"
  exit 1
fi

published_port="$(
  docker port "$POSTGRES_CONTAINER" 5432/tcp 2>/dev/null | head -n1 | awk -F: '{print $NF}'
)"
if [[ -z "${published_port:-}" ]]; then
  echo "[ERROR] Postgres container has no published host port for 5432/tcp."
  echo "[ERROR] Check POSTGRES_PORT in $DOCKER_ENV and docker compose config."
  exit 1
fi
if [[ "$published_port" != "$POSTGRES_PORT" ]]; then
  echo "[ERROR] Postgres published port mismatch. expected=$POSTGRES_PORT actual=$published_port"
  exit 1
fi
echo "[INFO] Postgres host port mapping verified: localhost:${POSTGRES_PORT} -> container:5432"

echo "[INFO] Ensuring PostgreSQL role and database exist..."
db_exists="$(
  docker exec -i "$POSTGRES_CONTAINER" psql -U "$POSTGRES_USER" -d postgres -tAc \
    "SELECT 1 FROM pg_database WHERE datname = '${POSTGRES_DB}'" | tr -d '[:space:]'
)"

if [[ "$db_exists" != "1" ]]; then
  docker exec -i "$POSTGRES_CONTAINER" psql -U "$POSTGRES_USER" -d postgres -v ON_ERROR_STOP=1 -c \
    "CREATE DATABASE \"${POSTGRES_DB}\""
  echo "[INFO] Created database ${POSTGRES_DB}"
else
  echo "[INFO] Database ${POSTGRES_DB} already exists"
fi

echo "[INFO] Setup complete"
echo "[INFO] Webhook DATABASE_URL => $DATABASE_URL"
echo
if [[ -x "$ROOT_DIR/backend/spring-api/mvnw" ]]; then
  echo "[INFO] Running Flyway via Spring Boot (non-web mode)..."
  (
    cd "$ROOT_DIR/backend/spring-api"
    ./mvnw -q -DskipTests -Dspring.main.web-application-type=none spring-boot:run
  )
  echo "[INFO] Flyway migration run completed"
else
  echo "[WARN] mvnw not found/executable at backend/spring-api/mvnw"
  echo "[WARN] Run Flyway manually with:"
  echo "  cd $ROOT_DIR/backend/spring-api && ./mvnw spring-boot:run"
fi
echo
echo "Next commands:"
echo "  cd $ROOT_DIR/integrations/github-webhook-service"
echo "  bun install"
echo "  bun run dev"
echo
echo "Quick verification:"
echo "  docker ps --format 'table {{.Names}}\t{{.Status}}\t{{.Ports}}'"
echo "  docker exec -it prflow-postgres psql -U $POSTGRES_USER -d $POSTGRES_DB -c '\\dt'"
