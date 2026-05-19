# Workflow Persistence Database Foundation (Step 1)

This step introduces persistence foundations for orchestration state using two core tables:
- `pull_requests` as the workflow state root.
- `pull_request_files` as the scope/intelligence substrate.

## Flyway migrations

Applied migration files:
- `V5__create_pull_requests.sql`
- `V6__create_pull_request_files.sql`
- `V7__add_updated_at_to_pull_request_files.sql`
- `V8__add_opened_at_index_to_pull_requests.sql`

### Ordering rationale

1. Base entities (`organizations`, `developers`, `repositories`) exist first.
2. `pull_requests` depends on `repositories` and `developers`.
3. `pull_request_files` depends on `pull_requests`.
4. Additive/index migrations run after base table creation.

## Why these tables matter

### 1) `pull_requests` is the workflow state root

`pull_requests` stores lifecycle state (`status`, `opened_at`, `merged_at`, `closed_at`) and identity (`repository_id`, `github_pr_number`). Every orchestration decision (assignment, prioritization, escalation, SLA tracking) anchors to this row.

### 2) `github_pr_number` is not globally unique

GitHub PR numbers are scoped per repository, not globally across GitHub. PR `#42` can exist in many repositories, so uniqueness must be `(repository_id, github_pr_number)`.

### 3) Workflow timestamps are critical

- `opened_at`: queue age, latency, SLA windows, load trends.
- `merged_at` / `closed_at`: throughput, lead time, completion state.
- `created_at` / `updated_at`: ingestion/audit tracking and reconciliation.

Without these timestamps, orchestration engines cannot compute age-based prioritization or lifecycle analytics.

## Why `pull_request_files` is strategically important

`pull_request_files` captures the changed surface area (`file_path`, `change_type`, `lines_added`, `lines_deleted`) and semantic scope (`scope_type`, `scope_identifier`). This enables:
- expertise matching from historical scope ownership,
- complexity signals from change volume and spread,
- assignment quality improvements via scope-aware routing.

### Why `scope_identifier` exists

`scope_identifier` is a normalized key for grouping changes into actionable review domains (examples: `backend:auth`, `frontend:checkout`, `infra:ci`). Path-only matching is brittle; scope identifiers support stable analytics and engine logic across file moves/refactors.

## Index strategy and orchestration impact

Indexes are designed for core orchestration access patterns:

- `pull_requests(repository_id)`: repo timeline and active queue lookups.
- `pull_requests(author_id)`: author-based load/reliability metrics.
- `pull_requests(status)`: active-state scans (`OPEN`, `DRAFT`, etc.).
- `pull_requests(opened_at)`: age/SLA prioritization and ordering.
- `pull_request_files(pull_request_id)`: hydrate files for a PR quickly.
- `pull_request_files(scope_identifier)`: expertise/scope analytics.
- `pull_request_files(file_path)`: file-level history and ownership lookups.

Without these indexes, queue scans and assignment computations degrade as data volume grows.

## Relational integrity guarantees

- `pull_requests.repository_id -> repositories.id` keeps PRs attached to valid repositories.
- `pull_requests.author_id -> developers.id` preserves author identity quality.
- `pull_request_files.pull_request_id -> pull_requests.id` prevents orphan file-change rows.

These constraints ensure orchestration engines operate on consistent, trustworthy graph relationships.

## Flyway behavior and schema versioning

Flyway tracks each migration in `flyway_schema_history`.

`flyway_schema_history` records:
- version,
- description,
- checksum,
- install order/time,
- success/failure state.

Why this matters:
- deterministic schema evolution across environments,
- auditable change history,
- safe, incremental deployments,
- drift detection when checksum/version diverges.

## Rollback considerations

Flyway favors forward-only migrations in production. Preferred rollback strategy:
1. create corrective follow-up migrations (safe forward fix),
2. avoid destructive down-migrations on live systems,
3. restore from backup only for severe incidents.

For this step, index additions are additive and low risk.

## Verification commands

### Docker/service checks

```bash
# Start/refresh DB stack
cd infra/docker && docker compose up -d postgres

# Verify backend can run Flyway migrations
cd /home/an/Desktop/dev/prflow/backend/spring-api
./mvnw -q -DskipTests spring-boot:run
```

### psql checks

```bash
# Connect
psql -h localhost -p 5432 -U prflow_app -d prflow_db
```

```sql
-- Migrations applied in order
SELECT installed_rank, version, description, success
FROM flyway_schema_history
ORDER BY installed_rank;

-- Tables exist
SELECT table_name
FROM information_schema.tables
WHERE table_schema = 'public'
  AND table_name IN ('pull_requests', 'pull_request_files')
ORDER BY table_name;

-- pull_requests columns
SELECT column_name, data_type, is_nullable, column_default
FROM information_schema.columns
WHERE table_name = 'pull_requests'
ORDER BY ordinal_position;

-- pull_request_files columns
SELECT column_name, data_type, is_nullable, column_default
FROM information_schema.columns
WHERE table_name = 'pull_request_files'
ORDER BY ordinal_position;

-- Foreign keys
SELECT conname, conrelid::regclass AS table_name, confrelid::regclass AS references_table
FROM pg_constraint
WHERE contype = 'f'
  AND conrelid::regclass::text IN ('pull_requests', 'pull_request_files')
ORDER BY conrelid::regclass::text, conname;

-- Uniques (including repository_id + github_pr_number)
SELECT conname, conrelid::regclass AS table_name, pg_get_constraintdef(oid) AS definition
FROM pg_constraint
WHERE contype = 'u'
  AND conrelid::regclass::text IN ('pull_requests', 'pull_request_files')
ORDER BY table_name, conname;

-- Indexes
SELECT tablename, indexname, indexdef
FROM pg_indexes
WHERE schemaname = 'public'
  AND tablename IN ('pull_requests', 'pull_request_files')
ORDER BY tablename, indexname;
```

## Preparedness for future engines

This schema prepares:
- Expertise engine: via scope/file history aggregation.
- Complexity engine: via per-file change volume and lifecycle timing.
- Assignment engine: via PR state, age, author context, and scope indexing.

The workflow state evolves from `OPENED` to terminal states (`MERGED`/`CLOSED`) with timestamped transitions. Orchestration systems depend on this structure to make deterministic, explainable, and performant decisions.
