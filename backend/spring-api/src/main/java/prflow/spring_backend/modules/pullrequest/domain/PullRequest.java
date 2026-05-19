package prflow.spring_backend.modules.pullrequest.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import prflow.spring_backend.entity.BaseEntity;
import prflow.spring_backend.enums.PullRequestStatus;
import prflow.spring_backend.modules.developer.domain.Developer;
import prflow.spring_backend.modules.repository.domain.Repository;

@Entity
@Table(
    name = "pull_requests",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uq_pull_requests_repository_number",
            columnNames = {"repository_id", "github_pr_number"}
        )
    },
    indexes = {
        @Index(name = "idx_pull_requests_repository_id", columnList = "repository_id"),
        @Index(name = "idx_pull_requests_author_id", columnList = "author_id"),
        @Index(name = "idx_pull_requests_status", columnList = "status"),
        @Index(name = "idx_pull_requests_opened_at", columnList = "opened_at")
    }
)
public class PullRequest extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "repository_id", nullable = false)
    private Repository repository;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Developer author;

    @Column(name = "github_pr_number", nullable = false)
    private Long githubPrNumber;

    @Column(name = "title", nullable = false, length = 500)
    private String title;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private PullRequestStatus status;

    @Column(name = "opened_at", nullable = false)
    private LocalDateTime openedAt;

    @Column(name = "merged_at")
    private LocalDateTime mergedAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @OneToMany(mappedBy = "pullRequest")
    private List<PullRequestFile> files = new ArrayList<>();

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public Developer getAuthor() {
        return author;
    }

    public void setAuthor(Developer author) {
        this.author = author;
    }

    public Long getGithubPrNumber() {
        return githubPrNumber;
    }

    public void setGithubPrNumber(Long githubPrNumber) {
        this.githubPrNumber = githubPrNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PullRequestStatus getStatus() {
        return status;
    }

    public void setStatus(PullRequestStatus status) {
        this.status = status;
    }

    public LocalDateTime getOpenedAt() {
        return openedAt;
    }

    public void setOpenedAt(LocalDateTime openedAt) {
        this.openedAt = openedAt;
    }

    public LocalDateTime getMergedAt() {
        return mergedAt;
    }

    public void setMergedAt(LocalDateTime mergedAt) {
        this.mergedAt = mergedAt;
    }

    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(LocalDateTime closedAt) {
        this.closedAt = closedAt;
    }

    public List<PullRequestFile> getFiles() {
        return files;
    }
}
