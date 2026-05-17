package prflow.spring_backend.modules.developer.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import prflow.spring_backend.entity.BaseEntity;
import prflow.spring_backend.modules.organization.domain.Organization;

@Entity
@Table(name = "developers")
public class Developer extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Column(name = "github_user_id", nullable = false)
    private Long githubUserId;

    @Column(name = "username", nullable = false, length = 255)
    private String username;

    @Column(name = "display_name", length = 255)
    private String displayName;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "review_capacity")
    private Integer reviewCapacity = 5;

    @Column(name = "reliability_score")
    private Double reliabilityScore = 0d;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "last_activity_at")
    private LocalDateTime lastActivityAt;

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public Long getGithubUserId() {
        return githubUserId;
    }

    public void setGithubUserId(Long githubUserId) {
        this.githubUserId = githubUserId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Integer getReviewCapacity() {
        return reviewCapacity;
    }

    public void setReviewCapacity(Integer reviewCapacity) {
        this.reviewCapacity = reviewCapacity;
    }

    public Double getReliabilityScore() {
        return reliabilityScore;
    }

    public void setReliabilityScore(Double reliabilityScore) {
        this.reliabilityScore = reliabilityScore;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }

    public LocalDateTime getLastActivityAt() {
        return lastActivityAt;
    }

    public void setLastActivityAt(LocalDateTime lastActivityAt) {
        this.lastActivityAt = lastActivityAt;
    }
}
