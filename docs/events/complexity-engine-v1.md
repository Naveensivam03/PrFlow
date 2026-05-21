# PRFlow Complexity Engine V1

## Architecture

Complexity is implemented as an isolated workflow intelligence engine under `engines/complexity`.

Flow:

1. `PULL_REQUEST_ANALYZED` is received by `PullRequestAnalyzedHandler`.
2. `ComplexityService` performs transactional, replay-safe enrichment.
3. `ComplexityCalculator` computes deterministic normalized scores.
4. `pull_requests` is updated with persisted complexity intelligence.
5. `COMPLEXITY_CALCULATED` is emitted for downstream engines.

## Why workflow intelligence differs from workflow persistence

Workflow persistence stores source-of-truth lifecycle state and organizational memory. Workflow intelligence derives explainable operational signals from that persisted state.

Complexity is intentionally not modeled as a core workflow entity because it is derived enrichment. Keeping it inside an engine preserves clean boundaries between durable state and computed decision signals.

## Why event-driven enrichment matters

Event chaining after successful persistence creates stable orchestration:

- committed state first, enrichment second
- loose coupling between ingestion/persistence and intelligence
- scalable pipeline composition for future engines

`COMPLEXITY_CALCULATED` becomes a reusable handoff event for expertise, assignment, and escalation engines.

## V1 deterministic signals

Active:

- `total_files_changed`
- `total_additions`
- `total_deletions`
- `unique_directories_touched`

Not active in V1 (placeholders only):

- cyclomatic complexity score
- coverage score

## Directory spread analysis

Directory is computed as parent path (`file_path` minus filename). Root files map to `.`.

Examples:

- `src/auth/login.ts`, `src/auth/token.ts` -> 1 directory (`src/auth`)
- `src/auth/login.ts`, `src/payments/stripe.ts`, `src/core/cache.ts` -> 3 directories

Why it matters:

- cross-module PRs widen failure surface
- distributed changes increase review context switching
- coordination and rollback risk increase with spread

## Normalization

Signals are normalized before weighted scoring to keep behavior bounded and predictable.

- Files changed: `0..5 -> 20`, `6..15 -> 60`, `16+ -> 100`
- Directory spread: `1 -> 20`, `2..4 -> 60`, `5+ -> 100`
- Additions/deletions: linear bounded normalization to `0..100` via configurable high-watermarks

Why normalization:

- stable orchestration behavior under outlier PR sizes
- deterministic explainability for tuning and incident review

## Scoring formulas

Long-term architecture target (kept as config placeholders):

`score = (diff_score * 0.30) + (dir_score * 0.25) + (cyclo_score * 0.25) + (coverage_score * 0.20)`

Real V1 implementation:

`final_score = (diff_score * 0.55) + (dir_score * 0.45)`

`diff_score` composition in V1:

- `file_count_score * 0.30`
- `additions_score * 0.50`
- `deletions_score * 0.20`

Additions intentionally weigh more than deletions because additions usually add cognitive load and new logic risk.

## Enum classification

`ComplexityLevel`:

- `0..20 -> LOW`
- `21..45 -> MEDIUM`
- `46..70 -> HIGH`
- `71..100 -> CRITICAL`

Numeric scores support future weight tuning. Enum levels optimize readability for orchestration decisions.

## Persistence model

Flyway migration extends `pull_requests`:

- `complexity_score NUMERIC(5,2)`
- `complexity_level VARCHAR(32)`
- `complexity_calculated_at TIMESTAMP`
- index: `idx_pull_requests_complexity_score`

Why persist intelligence snapshots:

- historical decisions remain reproducible
- trend analysis works across time
- replay/debug can use the original computed state

Why not dynamic historical recalculation:

- formula drift changes old outcomes retroactively
- threshold changes break auditability
- incident timelines become inconsistent

## Replay safety and idempotency

`ComplexityService` uses row locking and duplicate detection:

- lock PR row with `FOR UPDATE`
- if `complexity_calculated_at` already exists, log duplicate and skip
- otherwise compute, persist once, emit event once

This ensures deterministic mutation in event replay scenarios.

## Structured logging lifecycle

Engine logs include:

- event received
- normalization started
- scoring breakdown
- final score calculated
- persistence completed
- event emitted
- replay-safe duplicate handling
- failure propagation (transaction rollback)

These logs make it straightforward to trace lifecycle, compare expected vs actual scoring, and diagnose replay inconsistencies.

## Verification Guide

### 1. Migration + schema checks

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

### 2. Intelligence persistence checks

```sql
SELECT id, repository_id, github_pr_number,
       complexity_score, complexity_level, complexity_calculated_at
FROM pull_requests
ORDER BY id DESC
LIMIT 20;
```

### 3. Replay-event scenario

Replay the same `PULL_REQUEST_ANALYZED` twice for the same PR.

Expected:

- first run: compute + persist + emit `COMPLEXITY_CALCULATED`
- second run: duplicate log, no second mutation, no second calculated event

### 4. Example expected behavior

Scenario A (small PR):

- 2 files, 20 additions, 1 directory
- normalized: low file score, bounded low additions, low dir spread
- expected level: lower band (`LOW` with current defaults)

Scenario B (large PR):

- 25 files, 800 additions, 7 directories
- normalized: high file score, additions capped high, high dir spread
- expected level: `CRITICAL`

Scenario C (cross-module PR):

- changes across auth + payments + infrastructure
- directory spread increases strongly
- expected: higher score than single-module PR of similar line volume

## PRFlow trajectory

This engine is PRFlow's transition point from workflow persistence to workflow intelligence.

Next engines can subscribe to `COMPLEXITY_CALCULATED` and build:

- expertise fit enrichment
- assignment strategy enrichment
- escalation risk enrichment

without coupling those concerns into ingestion or persistence modules.
