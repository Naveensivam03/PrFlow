# Step 4: PR Persistence Orchestration Workflow

## Scope
This step implements orchestration workflow only:
- consume normalized `PULL_REQUEST_OPENED`
- coordinate transactional persistence
- extract and persist file scope metadata
- publish `PULL_REQUEST_ANALYZED`
- make webhook/event replay safe

Not implemented:
- complexity/expertise/assignment/escalation/analytics engines
- frontend

## Implemented components

- Handler/subscriber:
  - `src/events/pull-request-opened-handler.ts`
- Workflow service:
  - `src/services/pr-persistence-service.ts`
- Replay-safe webhook delivery gate:
  - `src/persistence/webhook-log-repository.ts`
  - `src/http/webhook-route.ts`
- Scope extraction utility:
  - `src/scope/scope-extractor.ts`
- Event chaining listener:
  - `src/events/pull-request-analyzed-handler.ts`
  - registration in `src/events/runtime.ts`

## Orchestration lifecycle

1. Webhook is normalized into internal event (`PULL_REQUEST_OPENED`).
2. Thin handler (`PullRequestOpenedHandler`) delegates to orchestration service.
3. Service gets installation token and fetches PR changed files from GitHub.
4. Service starts DB transaction.
5. Service acquires advisory transaction lock for `{owner}/{repo}#{prNumber}`.
6. Upsert sequence:
   - organization
   - repository
   - developer
   - pull request
7. File list is deduplicated by path and replaced atomically in `pull_request_files`.
8. Transaction commits.
9. Service emits internal `PULL_REQUEST_ANALYZED`.

## Why normalized events first

Normalization isolates external webhook shape from internal workflow contracts. Orchestration starts from stable internal semantics, not provider-specific payloads.

## Why thin handlers

Handlers should route and log only. Business mutation/coordination belongs in orchestration services to keep boundaries testable and maintainable.

## Why event-driven boundaries matter

Internal events decouple producers from downstream engines. `PULL_REQUEST_ANALYZED` creates a clean extension point for future complexity/expertise/assignment modules without coupling them to webhook ingestion.

## Transaction boundaries and consistency

Inside one DB transaction:
- repository/developer/PR upserts
- PR file replacement

Guarantees:
- all-or-nothing workflow mutation
- no partial PR/file state on failure
- rollback on any persistence error

`PULL_REQUEST_ANALYZED` is emitted only after successful commit, so chained consumers observe committed state.

## Idempotency and replay safety

### Delivery-level protection
`webhook_logs.delivery_id` has unique constraint. Insert now uses `ON CONFLICT DO NOTHING`, so redelivered webhook deliveries are safely ignored.

### Entity-level protection
- repository upsert on `(organization_id, github_repo_id)`
- developer upsert on `(organization_id, github_user_id)`
- PR upsert on `(repository_id, github_pr_number)`

### File-level protection
Workflow replaces PR files in-transaction and deduplicates fetched files by `filename` before insert.

### Concurrency protection
`pg_advisory_xact_lock(hashtext(owner/repo#pr))` serializes concurrent workflows mutating same PR aggregate.

Idempotency is critical because distributed webhook systems retry and redeliver by design.

## Scope extraction

Current strategy: deterministic folder scope (`scope_type=FOLDER`).

Examples:
- `src/auth/login.ts` -> `scope_identifier=src/auth`
- `src/payments/service/stripe.ts` -> `scope_identifier=src/payments/service`

This scope layer is foundational for future expertise and assignment intelligence.

## Structured logs and tracing checkpoints

Key logs:
- `GitHub webhook received`
- `Handling PULL_REQUEST_OPENED`
- `Starting PR persistence workflow`
- `PR persistence checkpoint` (`transaction_started`)
- `PR persistence checkpoint` (`files_persisted`, `fileCount`)
- `PR persistence workflow completed`
- `Publishing internal event` (`PULL_REQUEST_ANALYZED`)
- `Duplicate delivery ignored` (replay case)

Failure logs:
- `Webhook processing failed`
- `Event handler failed`
- `Failed to fetch PR files`

## Verification flow

### Type and startup checks
```bash
cd integrations/github-webhook-service
npm run typecheck
npm run dev
```

### SQL verification queries
```sql
-- PR persisted once (idempotent upsert)
SELECT repository_id, github_pr_number, status, opened_at, created_at, updated_at
FROM pull_requests
WHERE github_pr_number = <PR_NUMBER>
ORDER BY updated_at DESC;

-- Files persisted for PR
SELECT prf.file_path, prf.scope_type, prf.scope_identifier, prf.change_type, prf.lines_added, prf.lines_deleted
FROM pull_request_files prf
JOIN pull_requests pr ON pr.id = prf.pull_request_id
WHERE pr.github_pr_number = <PR_NUMBER>
ORDER BY prf.file_path;

-- Ensure no duplicate file rows per PR/path
SELECT prf.pull_request_id, prf.file_path, COUNT(*)
FROM pull_request_files prf
GROUP BY prf.pull_request_id, prf.file_path
HAVING COUNT(*) > 1;

-- Webhook replay tracking
SELECT delivery_id, event_type, processed, received_at
FROM webhook_logs
WHERE delivery_id = '<DELIVERY_ID>';
```

### psql commands
```bash
psql -h localhost -p 5432 -U prflow_app -d prflow_db
```

```sql
\dt
\d pull_requests
\d pull_request_files
\d webhook_logs
```

## Replay test scenarios

1. Duplicate webhook delivery id:
- Send same payload with same `x-github-delivery` twice.
- Expect second request to log `Duplicate delivery ignored` and return 202.

2. Same PR opened event with new delivery id:
- Redeliver payload with different delivery id.
- Expect PR upsert update (not duplicate row) and deterministic file replacement.

3. Concurrent duplicate deliveries:
- Trigger two opened deliveries quickly.
- Expect advisory lock serialization and consistent final PR/file state.

## Expected outcomes

- `pull_requests` persists correctly and remains unique per repo/pr number.
- `pull_request_files` persists with deterministic `FOLDER` scope.
- duplicate events are safe at delivery and aggregate levels.
- `PULL_REQUEST_ANALYZED` is emitted after successful transaction.

## Why this is foundational

PRFlow now has persistent workflow memory: normalized event -> transactional aggregate mutation -> chained internal event. This is the bridge from infrastructure plumbing into intelligence-ready orchestration for future complexity, expertise, and assignment engines.
