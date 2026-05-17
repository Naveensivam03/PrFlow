CREATE TABLE repositories (
    id BIGSERIAL PRIMARY KEY,
    organization_id BIGINT NOT NULL,
    github_repo_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    default_branch VARCHAR(255),
    expertise_mode VARCHAR(50) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_repositories_organization
        FOREIGN KEY (organization_id)
        REFERENCES organizations(id)
        ON DELETE CASCADE,
    CONSTRAINT uq_repositories_org_github_repo
        UNIQUE (organization_id, github_repo_id)
);

CREATE INDEX idx_repositories_organization_id ON repositories(organization_id);
CREATE INDEX idx_repositories_github_repo_id ON repositories(github_repo_id);
