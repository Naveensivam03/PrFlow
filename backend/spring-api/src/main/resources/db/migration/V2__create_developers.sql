CREATE TABLE developers (
    id BIGSERIAL PRIMARY KEY,
    organization_id BIGINT NOT NULL,
    github_user_id BIGINT NOT NULL,
    username VARCHAR(255) NOT NULL,
    display_name VARCHAR(255),
    avatar_url TEXT,
    review_capacity INTEGER DEFAULT 5,
    reliability_score DOUBLE PRECISION DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    last_activity_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_developers_organization
        FOREIGN KEY (organization_id)
        REFERENCES organizations(id)
        ON DELETE CASCADE,
    CONSTRAINT uq_developers_org_github_user
        UNIQUE (organization_id, github_user_id)
);

CREATE INDEX idx_developers_organization_id ON developers(organization_id);
CREATE INDEX idx_developers_github_user_id ON developers(github_user_id);
CREATE INDEX idx_developers_username ON developers(username);
