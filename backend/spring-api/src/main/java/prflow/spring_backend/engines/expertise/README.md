# Expertise Engine V1

## Purpose

The **Expertise Engine** is PRFlow's second workflow intelligence engine. It is responsible for building and updating persistent, deterministic organizational familiarity models based on real-world pull request history.

The engine answers the strategic question:
> **"Who actually understands this exact codebase area?"**
*(Rather than "who randomly touched this repository once?")*

---

## E1 Orchestration Workflow

```
ComplexityCalculatedEvent (Ingress)
  ↓
ComplexityCalculatedHandler
  ↓
ExpertiseService (Transactional Orchestration)
  ↓
ExpertiseCalculator (Deterministic Scoring & Bounded Normalization)
  ↓
Persist to developer_file_expertise (Replay-Safe Upsert)
  ↓
Emit EXPERTISE_CALCULATED (Egress Chaining)
```

---

## Architectural Rationale

### 1. Expertise is Organizational Intelligence
Unlike transient developer operational states, expertise represents long-term organizational memory. By mapping exact file changes to contributing developers, PRFlow aggregates a robust, queryable familiarity graph. This graph serves as a high-value strategic asset for intelligent routing and context-aware assignment.

### 2. Workflow Intelligence vs. Workflow Persistence
Ingestion/persistence modules (`modules/pullrequest`, `modules/repository`) own source-of-truth organizational memory and durable entity structures. Intelligence engines (`engines/expertise`, `engines/complexity`) own deterministic computation, score normalization, and decision routing. Keeping them isolated ensures that database tables storing core entities are decoupled from evolving scoring logic and heuristics.

### 3. File-Level Familiarity vs. Repository-Level Generic Scoring
Repository-level ownership is a weak heuristic because modern codebases contain widely disparate architectural zones (e.g., authentication, stripe payment gateways, data caching). Mapping developer contribution familiarity to the **file-level** and **folder-level (scope)** provides highly granular intelligence. This prevents routing a complex payments bug to a backend developer who has only written authentication files.

### 4. Recency Decay Weighting
Stale knowledge is less reliable as codebases evolve and developers' active context shifts. E1 implements a strict recency-decay formula to prioritize active familiarity:
- `< 30 days` $\to$ **1.0 weight** (Peak familiarity)
- `30–90 days` $\to$ **0.6 weight** (Warm familiarity)
- `90–180 days` $\to$ **0.4 weight** (Fading familiarity)
- `> 180 days` $\to$ **0.2 weight** (Legacy familiarity)

### 5. Why Expertise Must Persist Historically
Dynamic on-demand recalculation of expertise across all historical git/database records is highly resource-intensive and leads to auditing drift (formula tuning changes historical assignments retroactively). Persisting incremental snapshots at analysis time preserves reproducibility, provides stable audit trails for incident reviews, and enables fast query times.

### 6. Isolated Orchestration Engines
Keeping complexity, expertise, and future reviewer assignment engines strictly isolated from one another prevents "abstraction theater" and shared-utility chaos. Each engine is fully self-contained, event-driven, and highly maintainable.

---

## V1 Deterministic Scoring Formulas

### Exact File Familiarity Score
$$file\_touch\_score = \sum (recency\_weight_i) \quad \text{for all historical PRs touching } file\_path$$

### Scope Familiarity Score
$$scope\_touch\_score = \sum (recency\_weight_j) \quad \text{for all historical PRs touching } scope\_identifier$$

### Bounded Normalization
To prevent permanent domination of old contributors and infinite scoring loops, raw scores are normalized to a bounded $0..100$ scale using configurable high-watermarks (defaults: $10.0$ for files, $25.0$ for scopes):
$$normalized\_score = \min\left(100.0, \frac{\text{raw\_score} \times 100.0}{\text{high\_watermark}}\right)$$

### Final Expertise Combination
$$final\_expertise\_score = (\text{normalized\_file\_touch} \times 0.70) + (\text{normalized\_scope\_touch} \times 0.30)$$

---

## Event Egress: Reviewer Chaining

Upon successful calculation and persistence, the engine publishes the `EXPERTISE_CALCULATED` event.

### Event Payload Structure
- `pullRequestId`: Durable identifier of the PR.
- `repositoryId`: Repository identity.
- `expertiseCandidates`: Ranked list of developer IDs who have *any* file-level familiarity with the changed files in this PR.
- `expertiseScores`: Cumulative PR-level expertise score for each candidate, calculated as:
  $$pr\_expertise[dev] = \sum \text{expertise\_score}[dev][file] \quad \text{for all changed files}$$

This ranked candidate list is ready for downstream consumption by the **Reviewer Assignment Engine**, completing the E2E intelligence pipeline.
