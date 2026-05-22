package prflow.spring_backend.modules.pullrequest.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import prflow.spring_backend.entity.BaseEntity;
import prflow.spring_backend.modules.developer.domain.Developer;

@Entity
@Table(
    name = "pull_request_reviews",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uq_pull_request_reviews_github_id",
            columnNames = {"github_review_id"}
        )
    },
    indexes = {
        @Index(name = "idx_pr_reviews_pull_request_id", columnList = "pull_request_id"),
        @Index(name = "idx_pr_reviews_reviewer_id", columnList = "reviewer_id"),
        @Index(name = "idx_pr_reviews_review_state", columnList = "review_state"),
        @Index(name = "idx_pr_reviews_submitted_at", columnList = "review_submitted_at")
    }
)
public class PullRequestReview extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pull_request_id", nullable = false)
    private PullRequest pullRequest;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reviewer_id", nullable = false)
    private Developer reviewer;

    @Column(name = "github_review_id", nullable = false)
    private Long githubReviewId;

    @Column(name = "review_state", nullable = false, length = 50)
    private String reviewState; // APPROVED, COMMENTED, CHANGES_REQUESTED, DISMISSED

    @Column(name = "review_body", columnDefinition = "TEXT")
    private String reviewBody;

    @Column(name = "review_submitted_at", nullable = false)
    private LocalDateTime reviewSubmittedAt;

    public PullRequest getPullRequest() {
        return pullRequest;
    }

    public void setPullRequest(PullRequest pullRequest) {
        this.pullRequest = pullRequest;
    }

    public Developer getReviewer() {
        return reviewer;
    }

    public void setReviewer(Developer reviewer) {
        this.reviewer = reviewer;
    }

    public Long getGithubReviewId() {
        return githubReviewId;
    }

    public void setGithubReviewId(Long githubReviewId) {
        this.githubReviewId = githubReviewId;
    }

    public String getReviewState() {
        return reviewState;
    }

    public void setReviewState(String reviewState) {
        this.reviewState = reviewState;
    }

    public String getReviewBody() {
        return reviewBody;
    }

    public void setReviewBody(String reviewBody) {
        this.reviewBody = reviewBody;
    }

    public LocalDateTime getReviewSubmittedAt() {
        return reviewSubmittedAt;
    }

    public void setReviewSubmittedAt(LocalDateTime reviewSubmittedAt) {
        this.reviewSubmittedAt = reviewSubmittedAt;
    }
}
