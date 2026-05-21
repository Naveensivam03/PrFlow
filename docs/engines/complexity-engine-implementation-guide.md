# PRFlow Complexity Engine V1: Implementation Guide

## Document Purpose

This document explains exactly how Complexity Engine V1 is implemented in PRFlow, why it is architected as an isolated intelligence engine, and how to validate it in production-style environments.

It is intended for engineers implementing, reviewing, and operating PRFlow orchestration workflows.

## Scope and Boundaries

Complexity V1 is implemented only inside:

- `backend/spring-api/src/main/java/prflow/spring_backend/engines/complexity`

This is intentional. Complexity is **workflow intelligence**, not a core workflow entity.

### What belongs to modules vs engines

`modules/*` own durable workflow state and organizational memory:

- entities
- repositories
- lifecycle identity

`engines/*` own deterministic orchestration intelligence:

- enrichment computation
- decision signals
- event-driven chaining

Complexity belongs in `engines/complexity` because it is derived from persisted PR state and file deltas.

## Implementation Inventory

### Java engine files

- `ComplexityService.java`
- `ComplexityCalculator.java`
- `ComplexityBreakdown.java`
- `ComplexityLevel.java`
- `ComplexityCalculatedEvent.java`
- `PullRequestAnalyzedHandler.java`
- `ComplexityConfig.java`

### Schema migration

- `backend/spring-api/src/main/resources/db/migration/V9__add_complexity_intelligence_to_pull_requests.sql`

### Supporting docs

- `docs/events/complexity-engine-v1.md`
- this guide

## File-by-File Responsibilities

### Engine Source Files

- `ComplexityService.java`
  Executes transactional orchestration, replay guard, persistence update, and `COMPLEXITY_CALCULATED` emission.
- `ComplexityCalculator.java`
  Implements deterministic normalization and weighted scoring formulas.
- `ComplexityBreakdown.java`
  Captures full inspectable scoring breakdown for explainability and future tuning.
- `ComplexityLevel.java`
  Maps bounded numeric score to orchestration-friendly enum tier.
- `ComplexityCalculatedEvent.java`
  Defines payload contract for downstream enrichment chaining.
- `PullRequestAnalyzedHandler.java`
  Engine ingress listener for analyzed PR events, delegates to service.
- `ComplexityConfig.java`
  Holds engine-local thresholds and weights, including V1-active and future-placeholder entries.

### Schema + Validation Files

- `V9__add_complexity_intelligence_to_pull_requests.sql`
  Extends `pull_requests` with persisted intelligence snapshot fields and complexity index.
- `ComplexityCalculatorTest.java`
  Verifies deterministic scoring behavior, expected severity outcomes, and additions-vs-deletions weighting intent.

### Documentation Files

- `docs/events/complexity-engine-v1.md`
  Event workflow and operational verification reference.
- `docs/engines/complexity-engine-implementation-guide.md`
  Detailed implementation runbook and architecture rationale.

## End-to-End Workflow

1. PR persistence completes and `PULL_REQUEST_ANALYZED` is published.
2. `PullRequestAnalyzedHandler` receives the event.
3. `ComplexityService` loads and locks the target PR row.
4. Replay check runs (`complexity_calculated_at` guard).
5. Aggregate signals are computed from `pull_request_files`.
6. `ComplexityCalculator` normalizes signals and computes weighted scores.
7. `pull_requests` is updated with complexity intelligence fields.
8. `COMPLEXITY_CALCULATED` is emitted as an internal event.

This sequence ensures enrichment happens only after committed workflow persistence.

## Step-by-Step Implementation Details

## Step 1: Event ingress

`PullRequestAnalyzedHandler` is the engine ingress.

Responsibilities:

- subscribe to internal `PULL_REQUEST_ANALYZED`
- log event receipt with PR and repository identifiers
- delegate to `ComplexityService`

Why this matters:

- explicit orchestration entrypoint per engine
- easy to trace and test event boundaries

## Step 2: Transaction and replay-safe lock

`ComplexityService` starts a transaction and executes `SELECT ... FOR UPDATE` on the PR row.

Replay safety rule:

- if `complexity_calculated_at` is already set, treat as duplicate replay
- log duplicate handling
- skip mutation and skip emit

Why this matters:

- idempotent behavior under at-least-once delivery
- stable state mutation during retries/replays

## Step 3: Deterministic signal extraction

Service aggregates only these V1 signals from `pull_request_files`:

- `total_files_changed`
- `total_additions`
- `total_deletions`
- `unique_directories_touched`

Directory spread logic:

- parent directory of each `file_path`
- root file paths map to `.`
- distinct count = `unique_directories_touched`

Examples:

- `src/auth/login.ts`, `src/auth/token.ts` -> 1
- `src/auth/login.ts`, `src/payments/stripe.ts`, `src/core/cache.ts` -> 3

Why spread is weighted strongly:

- cross-module change surfaces raise operational risk
- reviewers must switch context across boundaries
- integration assumptions widen with distributed edits

## Step 4: Normalization

Raw values are not used directly in final scoring.

Normalization is bounded and deterministic:

- Files changed: `0..5 -> 20`, `6..15 -> 60`, `16+ -> 100`
- Directory spread: `1 -> 20`, `2..4 -> 60`, `5+ -> 100`
- Additions/deletions: linear bounded normalization to `0..100`, capped at `100`

Why normalization matters:

- giant PR outliers do not explode score scale
- score behavior stays stable for orchestration policies
- incident review remains explainable and reproducible

## Step 5: Weighted scoring

### Diff score (V1 active)

`diff_score = (file_count_score * 0.30) + (additions_score * 0.50) + (deletions_score * 0.20)`

Additions have higher weight than deletions because additions more often introduce new logic and review overhead.

### Final V1 score

`final_score = (diff_score * 0.55) + (dir_score * 0.45)`

### Future architecture placeholders (inactive in V1)

Retained in config and breakdown for evolution:

`score = (diff_score * 0.30) + (dir_score * 0.25) + (cyclo_score * 0.25) + (coverage_score * 0.20)`

Why placeholders now:

- keeps evolutionary path explicit
- prevents re-architecture churn in V2+
- preserves deterministic V1 behavior

## Step 6: Enum classification

Numeric score is mapped to `ComplexityLevel`:

- `0..20 -> LOW`
- `21..45 -> MEDIUM`
- `46..70 -> HIGH`
- `71..100 -> CRITICAL`

Why both numeric and enum:

- numeric score enables precise future tuning
- enum improves orchestration readability and policy mapping

## Step 7: Breakdown visibility

`ComplexityBreakdown` exposes:

- `file_count_score`
- `additions_score`
- `deletions_score`
- `diff_score`
- `directory_spread_score`
- `cyclo_placeholder_score`
- `coverage_placeholder_score`
- `final_score`
- `complexity_level`

Why inspectability matters:

- fast diagnosis of scoring inconsistencies
- evidence for tuning decisions
- reproducible audit trail for enrichment logic

## Step 8: Persistence as workflow intelligence snapshot

Persisted fields on `pull_requests`:

- `complexity_score NUMERIC(5,2)`
- `complexity_level VARCHAR(32)`
- `complexity_calculated_at TIMESTAMP`

Index:

- `idx_pull_requests_complexity_score`

Why persist derived intelligence:

- preserves historical orchestration context
- enables trend/risk analysis without recomputation drift
- supports deterministic replay debugging

Why dynamic historical recalculation is dangerous:

- formula changes would rewrite history
- threshold updates would invalidate prior decisions
- incident timelines become non-auditable

## Step 9: Event chaining output

After successful persistence, engine emits `COMPLEXITY_CALCULATED` with:

- `pullRequestId`
- `repositoryId`
- `complexityScore`
- `complexityLevel`

Why event chaining:

- loose coupling between engines
- independent deployment/evolution of enrichment consumers
- scalable intelligence pipeline composition

## Structured Logging Contract

Engine logs include:

- event received
- normalization started
- scoring breakdown
- final score calculated
- persistence completed
- event emitted
- replay-safe duplicate handling
- failure path via transactional exception propagation

Operational benefits:

- full lifecycle traceability per PR
- scoring discrepancy diagnosis
- replay/duplicate behavior verification

## Verification and Validation Steps

## Step A: Apply migrations

Run backend with Flyway enabled and ensure `V9` is applied.

## Step B: Verify schema

```sql
SELECT column_name, data_type
FROM information_schema.columns
WHERE table_name = 'pull_requests'
  AND column_name IN ('complexity_score', 'complexity_level', 'complexity_calculated_at')
ORDER BY column_name;

SELECT indexname, indexdef
FROM pg_indexes
WHERE tablename = 'pull_requests'
  AND indexname = 'idx_pull_requests_complexity_score';
```

## Step C: Trigger enrichment

Publish or replay a `PULL_REQUEST_ANALYZED` event for a PR that has persisted `pull_request_files` rows.

## Step D: Verify persistence

```sql
SELECT id, repository_id, github_pr_number,
       complexity_score, complexity_level, complexity_calculated_at
FROM pull_requests
ORDER BY id DESC
LIMIT 20;
```

## Step E: Verify replay idempotency

Replay the same analyzed event twice.

Expected results:

- first run: score persisted + event emitted
- second run: duplicate replay log, no second write, no second calculated event

## Step F: Verify scenario expectations

Scenario 1: Small PR

- input: 2 files, 20 additions, 1 directory
- expected: lower score band, `LOW` with current defaults

Scenario 2: Large PR

- input: 25 files, 800 additions, 7 directories
- expected: high bounded score, `CRITICAL`

Scenario 3: Cross-module PR

- input: auth + payments + infrastructure spread
- expected: higher score than a similar-volume single-module PR

## Operational Guidance

- Keep complexity logic inside `engines/complexity`.
- Do not leak scoring logic into workflow entities.
- Prefer additive migrations and forward-only schema evolution.
- Keep thresholds and weights deterministic and observable.
- Evolve scoring incrementally, preserving snapshot history.

## PRFlow Roadmap Fit

Complexity Engine V1 is the first intelligence layer above persisted workflow state.

It prepares a clean event contract for future engines to consume `COMPLEXITY_CALCULATED`:

- expertise intelligence
- assignment intelligence
- escalation intelligence

This is the architectural progression from workflow persistence into event-driven orchestration intelligence.
