package prflow.spring_backend.modules.repository.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import prflow.spring_backend.entity.BaseEntity;
import prflow.spring_backend.enums.ExpertiseMode;
import prflow.spring_backend.modules.organization.domain.Organization;

@Entity
@Table(name = "repositories")
public class Repository extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false) //since multiple repositories can be under same organization.
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Column(name = "github_repo_id", nullable = false)
    private Long githubRepoId;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "default_branch", length = 255)
    private String defaultBranch;

    @Enumerated(EnumType.STRING)
    @Column(name = "expertise_mode", nullable = false, length = 50)
    private ExpertiseMode expertiseMode;

    @Column(name = "is_active")
    private Boolean isActive = true;

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public Long getGithubRepoId() {
        return githubRepoId;
    }

    public void setGithubRepoId(Long githubRepoId) {
        this.githubRepoId = githubRepoId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDefaultBranch() {
        return defaultBranch;
    }

    public void setDefaultBranch(String defaultBranch) {
        this.defaultBranch = defaultBranch;
    }

    public ExpertiseMode getExpertiseMode() {
        return expertiseMode;
    }

    public void setExpertiseMode(ExpertiseMode expertiseMode) {
        this.expertiseMode = expertiseMode;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }
}
