-- Create persistent pull request reviews table to support review intelligence
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

-- Performance indexes for review intelligence, indexing by pull request, reviewer, and state
CREATE INDEX idx_pr_reviews_pull_request_id ON pull_request_reviews(pull_request_id);
CREATE INDEX idx_pr_reviews_reviewer_id ON pull_request_reviews(reviewer_id);
CREATE INDEX idx_pr_reviews_review_state ON pull_request_reviews(review_state);
CREATE INDEX idx_pr_reviews_submitted_at ON pull_request_reviews(review_submitted_at);
