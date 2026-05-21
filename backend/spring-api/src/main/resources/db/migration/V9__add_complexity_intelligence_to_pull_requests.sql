-- Complexity Engine V1 persistence snapshot fields.
-- These columns store derived workflow intelligence at calculation time.
ALTER TABLE pull_requests
    ADD COLUMN complexity_score NUMERIC(5,2),
    ADD COLUMN complexity_level VARCHAR(32),
    ADD COLUMN complexity_calculated_at TIMESTAMP;

-- Supports queueing/ranking/filtering by computed complexity.
CREATE INDEX idx_pull_requests_complexity_score ON pull_requests(complexity_score);
