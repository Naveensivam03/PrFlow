# Review Intelligence Foundation

The **Review Intelligence Foundation** establishes **organizational review memory** within PRFlow. It tracks who reviews which scopes, who approves which modules, and accumulates reviewer expertise in a completely deterministic, transactionally consistent, and replay-safe manner.

---

## Architecture & Data Flow

When a pull request is analyzed, submitted, or merged, the platform triggers a synchronized update cycle. The orchestration flow processes events as follows:

```mermaid
sequenceDiagram
    participant GH as GitHub Webhook Ingress
    participant Ingress as PullRequestEventController
    participant Sync as ReviewSyncService
    participant Client as GitHubReviewFetcher
    participant Expertise as ReviewExpertiseEnricher
    participant DB as PostgreSQL

    GH->>Ingress: POST /api/events/review-submitted
    Note over Ingress: Event published to Spring ApplicationEventPublisher
    Ingress->>Sync: onReviewSubmitted(ReviewSubmittedEvent)
    Sync->>Client: fetchReviews(org, repo, prNum)
    Client-->>Sync: Return List<GitHubReviewDto> (Paginated)
    
    loop Each Review
        Sync->>DB: Upsert Developer (ON CONFLICT DO UPDATE)
        Sync->>DB: Check Replay (Query existing state)
        Sync->>DB: Upsert Review (ON CONFLICT DO UPDATE)
    end
    
    loop Each Unique Reviewer
        Sync->>Expertise: enrichDeveloperExpertise(reviewerId, repositoryId)
        Note over Expertise: Calculate totals from scratch to ensure absolute replay safety
        Expertise->>DB: Recalculate and Upsert developer_file_expertise
    end
    
    Sync->>Ingress: Emit REVIEWS_SYNCHRONIZED Event
```

---

## Database Design

Reviews are stored permanently in the `pull_request_reviews` table. A cascade delete constraint on developers and pull requests ensures database consistency, while high-efficiency indexes optimize fast lookups on state and timestamp fields.

### `pull_request_reviews` Schema
```sql
CREATE TABLE pull_request_reviews (
    id BIGSERIAL PRIMARY KEY,
    pull_request_id BIGINT NOT NULL,
    reviewer_id BIGINT NOT NULL,
    github_review_id BIGINT NOT NULL,
    review_state VARCHAR(50) NOT NULL,
    review_body TEXT,
    review_submitted_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_pull_request_reviews_github_id UNIQUE (github_review_id),
    CONSTRAINT fk_pull_request_reviews_pull_request
        FOREIGN KEY (pull_request_id)
        REFERENCES pull_requests(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_pull_request_reviews_reviewer
        FOREIGN KEY (reviewer_id)
        REFERENCES developers(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_pr_reviews_pull_request_id ON pull_request_reviews(pull_request_id);
CREATE INDEX idx_pr_reviews_reviewer_id ON pull_request_reviews(reviewer_id);
CREATE INDEX idx_pr_reviews_review_state ON pull_request_reviews(review_state);
CREATE INDEX idx_pr_reviews_submitted_at ON pull_request_reviews(review_submitted_at);
```

---

## Zero-Dependency GitHub App Authentication

PRFlow avoids heavy third-party JWT libraries to keep the system simple and robust. `GitHubAuthService` utilizes the standard Java `java.security.Signature` API and a built-in ASN.1 DER parser to read private keys in both standard formats:
* **PKCS#8** (`-----BEGIN PRIVATE KEY-----`)
* **PKCS#1** (`-----BEGIN RSA PRIVATE KEY-----`)

It generates a valid RSA-256 JWT using standard Base64 encoding, requests an installation access token, and caches it in memory.

---

## Deterministic Expertise Enrichment & Replay Safety

To avoid duplicate-accumulation bugs caused by replayed webhooks, retries, or manual synchronizations, **reviewer expertise is calculated from scratch** from the persisted database records whenever an update runs.

### Mathematical Formula
A developer's total expertise score on a file path is:
$$\text{total\_expertise\_score} = \text{touch\_score} + \text{review\_score}$$

Where:
* $\text{touch\_score}$ is calculated from authored PR file-touches (decayed according to recency).
* $\text{review\_score}$ is calculated from standard counts and weights:
  $$\text{review\_score} = (\text{total\_reviews\_count} \times 1.0) + (\text{approvals\_count} \times 2.0)$$

This guarantees **100% idempotency and replay safety**: re-running a sync task results in exactly the same stable score.

---

## Operational Verification

### Review Inspection Queries
Operators can run these SQL statements directly against the PostgreSQL instance to monitor accumulated organizational intelligence:

```sql
-- View all reviews sorted by submission time
SELECT 
    d.username AS reviewer, 
    pr.title AS pr_title, 
    pr_rev.review_state, 
    pr_rev.review_submitted_at
FROM pull_request_reviews pr_rev
JOIN pull_requests pr ON pr_rev.pull_request_id = pr.id
JOIN developers d ON pr_rev.reviewer_id = d.id
ORDER BY pr_rev.review_submitted_at DESC;

-- View developer expertise score breakdown by file path
SELECT 
    d.username, 
    dfe.file_path, 
    dfe.scope_identifier, 
    dfe.expertise_score, 
    dfe.last_activity_at
FROM developer_file_expertise dfe
JOIN developers d ON dfe.developer_id = d.id
ORDER BY dfe.expertise_score DESC;
```
