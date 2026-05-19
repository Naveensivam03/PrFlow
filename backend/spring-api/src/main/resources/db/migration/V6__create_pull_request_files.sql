CREATE TABLE pull_request_files (
    id BIGSERIAL PRIMARY KEY,
    pull_request_id BIGINT NOT NULL,
    file_path TEXT NOT NULL,
    scope_type VARCHAR(50) NOT NULL,
    scope_identifier TEXT NOT NULL,
    change_type VARCHAR(50) NOT NULL,
    lines_added INTEGER NOT NULL DEFAULT 0,
    lines_deleted INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pull_request_files_pull_request
        FOREIGN KEY (pull_request_id)
        REFERENCES pull_requests(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_pull_request_files_pull_request_id ON pull_request_files(pull_request_id);
CREATE INDEX idx_pull_request_files_scope_identifier ON pull_request_files(scope_identifier);
CREATE INDEX idx_pull_request_files_file_path ON pull_request_files(file_path);
