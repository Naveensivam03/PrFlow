# Complexity Engine V1

## Purpose

This engine is PRFlow's first workflow intelligence engine. It enriches already-persisted pull request workflow state with deterministic complexity intelligence.

Pipeline:

`PULL_REQUEST_ANALYZED` -> `PullRequestAnalyzedHandler` -> `ComplexityService` -> `ComplexityCalculator` -> persist complexity fields -> emit `COMPLEXITY_CALCULATED`

## Why intelligence differs from persistence

Workflow persistence stores durable state transitions and organizational memory (`pull_requests`, `pull_request_files`).

Workflow intelligence computes explainable decision signals from persisted state. Complexity is not a workflow entity because it is derived, recomputable intelligence, not source-of-truth workflow identity.

## Why event-driven enrichment matters

Enrichment after persistence guarantees:
- downstream engines read committed data
- intelligence can evolve without rewriting ingestion
- chained orchestration stays loosely coupled

## V1 deterministic signals

Active in V1:
- `total_files_changed`
- `total_additions`
- `total_deletions`
- `unique_directories_touched`

Future placeholders only:
- cyclomatic complexity
- coverage risk

## Normalization and scoring

Signals are normalized to bounded 0..100 scores to avoid unstable behavior from giant PR outliers.

File-count normalization:
- 0..5 -> 20
- 6..15 -> 60
- 16+ -> 100

Directory-spread normalization:
- 1 -> 20
- 2..4 -> 60
- 5+ -> 100

Additions/deletions normalization:
- bounded by configurable high-watermarks
- capped at 100
- additions weighted higher than deletions in `diff_score`

`diff_score`:
- `file_count_score * 0.30`
- `additions_score * 0.50`
- `deletions_score * 0.20`

Real V1 final formula:

`final_score = (diff_score * 0.55) + (dir_score * 0.45)`

Future target architecture retained in config (inactive in V1):

`score = (diff_score * 0.30) + (dir_score * 0.25) + (cyclo_score * 0.25) + (coverage_score * 0.20)`

## Complexity levels

Numeric score is mapped to enum level:
- 0..20: `LOW`
- 21..45: `MEDIUM`
- 46..70: `HIGH`
- 71..100: `CRITICAL`

Numeric scores provide tuning precision, while enum levels provide orchestration readability.

## Directory spread importance

Unique directory spread approximates cross-module change surface.

Cross-module PRs are operationally riskier because review context shifts across boundaries, dependency assumptions widen, and coordination cost rises. This is why directory spread has a high weight in V1.

## Replay safety

The service takes a row lock on the PR and skips duplicates when `complexity_calculated_at` already exists. This prevents replay storms from producing repeated writes or inconsistent intelligence.
