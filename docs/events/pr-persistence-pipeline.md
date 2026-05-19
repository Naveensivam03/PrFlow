# PR Persistence Pipeline (Task 7)

## Purpose

Persist GitHub pull request workflow state in PostgreSQL so PRFlow has durable orchestration state for downstream engines.

## Scope (This Task)

Implemented now:

- PR workflow persistence (`pull_requests`)
- PR file persistence (`pull_request_files`)
- Folder-based scope extraction (`FOLDER` only)
- Internal chaining: `PULL_REQUEST_OPENED` -> `PULL_REQUEST_ANALYZED`

Not implemented now:

- complexity engine
- expertise engine
- assignment engine
- analytics engine

## Architecture Decisions

1. GitHub remains source of truth.
2. Webhook service ingests and normalizes events.
3. Persistence happens in one transactional unit for consistency.
4. Internal event chaining is in-memory only.

## Database Design

### `pull_requests`

Stores workflow-level PR state:

- identity: `repository_id`, `github_pr_number`
- ownership: `author_id`
- state: `status`, `opened_at`, `merged_at`, `closed_at`
- content: `title`, `description`

Why table exists:

- foundation for workflow state evolution over PR lifetime
- canonical join point for files, complexity, expertise, assignment later

### `pull_request_files`

Stores changed-file facts for each PR:

- `file_path`
- `change_type`
- `lines_added`, `lines_deleted`
- `scope_type`, `scope_identifier`

Why table exists:

- deterministic input for future complexity and expertise analysis
- decouples external GitHub API dependency from downstream computation

## Event Lifecycle

1. GitHub sends `pull_request` (`opened`).
2. Webhook service verifies signature and validates payload.
3. Payload normalized to `PULL_REQUEST_OPENED`.
4. Persistence service:
   - upserts organization
   - upserts repository
   - upserts developer
   - upserts pull request
   - fetches changed files from GitHub API
   - replaces PR file rows
5. On success, publishes `PULL_REQUEST_ANALYZED`.

## Why Transactional Persistence Matters

PR + file rows must represent one consistent snapshot. Without a transaction, partial writes can cause downstream engines to read inconsistent state.

## GitHub API Client Behavior

Endpoint used:

- `GET /repos/{owner}/{repo}/pulls/{pull_number}/files`

Auth:

- create app JWT
- exchange for installation access token
- call PR files endpoint with installation token

Reliability:

- retries transient failures (3 attempts)
- logs delivery id + attempt metadata

## Idempotency Strategy

- `pull_requests`: `ON CONFLICT (repository_id, github_pr_number) DO UPDATE`
- `pull_request_files`: delete existing rows for PR, then insert current snapshot

Repeated delivery/replay produces same end state.

## Expected Logs

Success path:

- `GitHub webhook received`
- `Publishing internal event` (`PULL_REQUEST_OPENED`)
- `Starting PR persistence workflow`
- `Fetched PR files from GitHub`
- `PR persistence workflow completed`
- `PULL_REQUEST_ANALYZED published`
- `Webhook processed`

## Common Failure Scenarios

1. GitHub API failure
- Cause: network/rate limit/5xx
- Behavior: retry, then fail request processing

2. Invalid installation token
- Cause: bad app key/app id or installation mismatch
- Behavior: token fetch fails, no DB commit

3. Duplicate webhook deliveries
- Cause: GitHub retry/replay
- Behavior: idempotent upserts preserve consistent state

4. DB transaction failure
- Cause: constraint/connectivity issue
- Behavior: whole transaction rolls back, no partial PR snapshot

5. Missing org/install context in payload
- Cause: incomplete payload assumptions
- Behavior: persistence handler fails fast with explicit log error

## Debugging Guide

### Verify migrations

```bash
psql -U prflow_app -d prflow_db -c "SELECT version, description, success FROM flyway_schema_history ORDER BY installed_rank;"
```

### Verify tables

```bash
psql -U prflow_app -d prflow_db -c "\\dt pull_requests"
psql -U prflow_app -d prflow_db -c "\\dt pull_request_files"
```

### Inspect persisted PR

```bash
psql -U prflow_app -d prflow_db -c "SELECT id, repository_id, github_pr_number, status, opened_at FROM pull_requests ORDER BY id DESC LIMIT 20;"
```

### Inspect files + scopes

```bash
psql -U prflow_app -d prflow_db -c "SELECT pull_request_id, file_path, scope_type, scope_identifier, lines_added, lines_deleted FROM pull_request_files ORDER BY id DESC LIMIT 50;"
```

## How This Evolves to Future Engines

`PULL_REQUEST_ANALYZED` is the stable handoff point for downstream engines. They can subscribe later without changing ingestion/persistence contracts.
