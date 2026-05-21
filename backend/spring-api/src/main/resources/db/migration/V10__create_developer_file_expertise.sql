-- Expertise Engine V1 persistent organizational memory schema.
-- Maps historical contributions of developers to granular code scopes and files.
CREATE TABLE developer_file_expertise (
    id BIGSERIAL PRIMARY KEY,
    developer_id BIGINT NOT NULL,
    repository_id BIGINT NOT NULL,
    file_path TEXT NOT NULL,
    scope_identifier TEXT NOT NULL,
    expertise_score NUMERIC(5,2) NOT NULL,
    last_activity_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_developer_file_expertise_developer
        FOREIGN KEY (developer_id)
        REFERENCES developers(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_developer_file_expertise_repository
        FOREIGN KEY (repository_id)
        REFERENCES repositories(id)
        ON DELETE CASCADE,
    CONSTRAINT uq_developer_file_expertise_dev_repo_file
        UNIQUE (developer_id, repository_id, file_path)
);

-- Accelerates reviewer candidate queries, score ranking, and scope lookup.
CREATE INDEX idx_developer_file_expertise_developer_id ON developer_file_expertise(developer_id);
CREATE INDEX idx_developer_file_expertise_repository_id ON developer_file_expertise(repository_id);
CREATE INDEX idx_developer_file_expertise_scope_identifier ON developer_file_expertise(scope_identifier);
CREATE INDEX idx_developer_file_expertise_score ON developer_file_expertise(expertise_score);
