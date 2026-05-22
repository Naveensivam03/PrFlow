-- Alter developers table to support seniority ranking
ALTER TABLE developers
    ADD COLUMN seniority VARCHAR(50) DEFAULT 'MID' NOT NULL;

-- Create persistent reviewer assignments table (idempotent, relational target)
CREATE TABLE reviewer_assignments (
    id BIGSERIAL PRIMARY KEY,
    pull_request_id BIGINT NOT NULL,
    developer_id BIGINT NOT NULL,
    assignment_score DOUBLE PRECISION NOT NULL,
    assignment_reason TEXT NOT NULL,
    assignment_type VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_reviewer_assignments_pr_dev 
        UNIQUE (pull_request_id, developer_id),
    CONSTRAINT fk_reviewer_assignments_pull_request
        FOREIGN KEY (pull_request_id)
        REFERENCES pull_requests(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_reviewer_assignments_developer
        FOREIGN KEY (developer_id)
        REFERENCES developers(id)
        ON DELETE CASCADE
);

-- Build performance and lookup indices
CREATE INDEX idx_reviewer_assignments_pull_request_id ON reviewer_assignments(pull_request_id);
CREATE INDEX idx_reviewer_assignments_developer_id ON reviewer_assignments(developer_id);
CREATE INDEX idx_reviewer_assignments_score ON reviewer_assignments(assignment_score);
