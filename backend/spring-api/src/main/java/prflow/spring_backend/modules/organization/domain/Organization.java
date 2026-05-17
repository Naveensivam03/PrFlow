package prflow.spring_backend.modules.organization.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.LinkedHashSet;
import java.util.Set;
import prflow.spring_backend.entity.BaseEntity;
import prflow.spring_backend.modules.developer.domain.Developer;
import prflow.spring_backend.modules.repository.domain.Repository;

@Entity
@Table(name = "organizations")
public class Organization extends BaseEntity {

    @Column(name = "github_installation_id", nullable = false, unique = true)
    private Long githubInstallationId;

    @Column(name = "github_org_id", nullable = false, unique = true)
    private Long githubOrgId;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "plan_type", length = 50)
    private String planType;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @OneToMany(mappedBy = "organization")
    private Set<Developer> developers = new LinkedHashSet<>();

    @OneToMany(mappedBy = "organization")
    private Set<Repository> repositories = new LinkedHashSet<>();

    public Long getGithubInstallationId() {
        return githubInstallationId;
    }

    public void setGithubInstallationId(Long githubInstallationId) {
        this.githubInstallationId = githubInstallationId;
    }

    public Long getGithubOrgId() {
        return githubOrgId;
    }

    public void setGithubOrgId(Long githubOrgId) {
        this.githubOrgId = githubOrgId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlanType() {
        return planType;
    }

    public void setPlanType(String planType) {
        this.planType = planType;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }

    public Set<Developer> getDevelopers() {
        return developers;
    }

    public Set<Repository> getRepositories() {
        return repositories;
    }
}
