# Current Engine Architecture Analysis - PRFlow

## 1. Complexity Engine (V1)
The Complexity Engine provides the first layer of intelligence by scoring PRs based on structural signals.

*   **Isolation**: Excellent. It relies solely on `pull_request_files` and basic PR metadata.
*   **Determinism**: High. The `ComplexityCalculator` uses a weighted formula based on:
    *   File count tiers.
    *   Addition/Deletion volume.
    *   Directory spread (change locality).
*   **Persistence**: Stores `complexity_score` and `complexity_level` directly on the `pull_requests` table.
*   **Expansion**: Includes placeholders for Cyclomatic Complexity and Test Coverage, indicating a planned move toward deeper AST-based or CI-integrated analysis.

## 2. Expertise Engine (V1)
The Expertise Engine builds organizational memory by tracking developer familiarity with code paths.

*   **Logic**: It calculates expertise for the **PR Author** by looking at their historical touches on the files changed in the current PR.
*   **Decay Mechanism**: Implements a time-based decay (e.g., touches in the last 30 days are weighted higher than those from 180+ days ago). This ensures that "expertise" reflects current knowledge, not just ancient history.
*   **Scoring Boundary**:
    *   **File Level**: Specific familiarity with a file.
    *   **Scope Level**: General familiarity with a directory or module.
*   **Orchestration Role**: It identifies **other candidates** who have expertise in the same files, effectively generating a "Shortlist" of reviewers.

## 3. Structural Evaluation

### Separation of Concerns
*   **Workflow Memory**: Handled by the Webhook service (storing the "What").
*   **Intelligence Layer**: Handled by Spring Services (calculating the "Meaning").
*   This separation is clean. The Spring engines don't care *how* the files were fetched; they only care that they are in the database.

### Event Chaining Scalability
*   The current chaining (`PRA -> Complexity -> Expertise`) is linear.
*   **Scalability Concern**: As more engines are added (Security, Performance, Documentation), a linear chain becomes brittle.
*   **Better Approach**: Engines should ideally run in parallel after `PULL_REQUEST_ANALYZED` is emitted, and a final "Aggregator" or "Assignment Engine" should wait for all required intelligence signals to be present.

## 4. Future Engine Dependencies

| Engine | Depends On | Purpose |
| :--- | :--- | :--- |
| **Complexity** | `pull_request_files` | Determine PR weight. |
| **Expertise** | `pull_request_files`, `pull_requests` | Identify knowledge owners. |
| **Assignment** | Complexity, Expertise, Developer Availability | Select best reviewer. |
