# System Evolution & Strategic Gaps - PRFlow

## 1. System Maturity Stages

PRFlow is evolving through five distinct stages. The system currently sits between Stage 2 and Stage 3.

1.  **Stage 1: Ingestion** (COMPLETE)
    *   GitHub Webhook integration, event normalization.
2.  **Stage 2: Workflow Persistence** (COMPLETE)
    *   PostgreSQL schema for PRs and Files, storage of raw signals.
3.  **Stage 3: Workflow Intelligence** (IN PROGRESS)
    *   Complexity scoring, initial Expertise accumulation.
4.  **Stage 4: Organizational Intelligence** (GAPS IDENTIFIED)
    *   Full knowledge graph of members, contributors, and cross-repo expertise.
5.  **Stage 5: Orchestration Automation** (FUTURE)
    *   Auto-assignment, auto-labeling, intelligent routing.

## 2. Critical Architectural Gaps

### G1: Contributor Synchronization Gap
*   **Problem**: PRFlow only knows about developers when they author a PR.
*   **Impact**: Potential reviewers who are prolific "review-only" contributors or who haven't opened a PR recently are invisible to the Expertise Engine.
*   **Requirement**: A background synchronization job to fetch all repository contributors and organization members.

### G2: Organizational Visibility Gap
*   **Problem**: There is no explicit mapping of "Developer X is a member of Repository Y".
*   **Impact**: The Assignment Engine won't know the valid "candidate pool" for a review beyond the author's previous PR history.
*   **Requirement**: A `repository_developers` relationship table and sync logic.

### G3: Expertise Backfill Limitation
*   **Problem**: Expertise only accumulates from the moment PRFlow is installed.
*   **Impact**: On Day 1, PRFlow has zero expertise data, making reviewer suggestions useless for several weeks/months.
*   **Requirement**: A "Historical Sync" feature to analyze the last ~500 PRs of a repository upon installation to bootstrap the Expertise Engine.

### G4: Orchestration Scaling & Reliability
*   **Problem**: Events are dispatched in-memory in the Webhook service and forwarded via a simple HTTP POST.
*   **Impact**: No retry logic, no dead-letter queues, and high risk of "silent failures" in the intelligence pipeline.
*   **Requirement**: Transition to a persistent event bus (e.g., Outbox pattern in Postgres or a dedicated Message Broker).

## 3. Summary of Missing Organizational Graph Data

| Data Point | Current State | Required For |
| :--- | :--- | :--- |
| **Repo Contributors** | Missing | Identifying potential reviewers. |
| **Org Membership** | Missing | Global reviewer pool discovery. |
| **Team Structure** | Missing | Team-based review routing. |
| **Historical Expertise** | Missing | "Day 1" value for new installations. |
| **Availability/Load** | Missing | Balancing review assignments. |
