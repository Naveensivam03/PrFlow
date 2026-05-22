CREATE TABLE repository_developers (
    id BIGSERIAL PRIMARY KEY,
    repository_id BIGINT NOT NULL,
    developer_id BIGINT NOT NULL,
    contribution_count INTEGER DEFAULT 0,
    last_contributed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_repo_dev_repository
        FOREIGN KEY (repository_id)
        REFERENCES repositories(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_repo_dev_developer
        FOREIGN KEY (developer_id)
        REFERENCES developers(id)
        ON DELETE CASCADE,
    CONSTRAINT uq_repository_developer
        UNIQUE (repository_id, developer_id)
);

CREATE INDEX idx_repository_developers_repository_id ON repository_developers(repository_id);
CREATE INDEX idx_repository_developers_developer_id ON repository_developers(developer_id);
