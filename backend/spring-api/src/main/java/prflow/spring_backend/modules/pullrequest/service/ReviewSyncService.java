package prflow.spring_backend.modules.pullrequest.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prflow.spring_backend.config.GitHubAuthService;
import prflow.spring_backend.engines.expertise.ReviewExpertiseEnricher;
import prflow.spring_backend.modules.pullrequest.event.ReviewsSynchronizedEvent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewSyncService {

    private static final Logger logger = LoggerFactory.getLogger(ReviewSyncService.class);

    private final JdbcTemplate jdbcTemplate;
    private final GitHubAuthService authService;
    private final GitHubReviewFetcher reviewFetcher;
    private final ReviewExpertiseEnricher expertiseEnricher;
    private final ApplicationEventPublisher eventPublisher;

    public ReviewSyncService(
        JdbcTemplate jdbcTemplate,
        GitHubAuthService authService,
        GitHubReviewFetcher reviewFetcher,
        ReviewExpertiseEnricher expertiseEnricher,
        ApplicationEventPublisher eventPublisher
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.authService = authService;
        this.reviewFetcher = reviewFetcher;
        this.expertiseEnricher = expertiseEnricher;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Executes the full transactional review synchronization workflow for a given pull request.
     */
    @Transactional
    public void synchronizeReviews(Long pullRequestId) {
        System.out.println("[DEBUG][ReviewSyncService] service.start pullRequestId=" + pullRequestId);
        logger.info("review.sync.started pullRequestId={}", pullRequestId);

        try {
            // 1. Fetch Pull Request metadata joined with repository and organization
            String sqlMetadata = 
                "SELECT " +
                "  pr.github_pr_number, " +
                "  r.github_repo_id, " +
                "  r.name as repo_name, " +
                "  o.github_installation_id, " +
                "  o.name as org_name, " +
                "  pr.repository_id, " +
                "  r.organization_id " +
                "FROM pull_requests pr " +
                "JOIN repositories r ON pr.repository_id = r.id " +
                "JOIN organizations o ON r.organization_id = o.id " +
                "WHERE pr.id = ?";

            PullRequestSyncMeta meta = jdbcTemplate.query(
                sqlMetadata,
                (rs, rowNum) -> new PullRequestSyncMeta(
                    rs.getLong("github_pr_number"),
                    rs.getLong("github_repo_id"),
                    rs.getString("repo_name"),
                    rs.getLong("github_installation_id"),
                    rs.getString("org_name"),
                    rs.getLong("repository_id"),
                    rs.getLong("organization_id")
                ),
                pullRequestId
            ).stream().findFirst().orElseThrow(() -> 
                new IllegalArgumentException("Pull request not found for id: " + pullRequestId)
            );

            // 2. Fetch GitHub App installation token
            String token = authService.getInstallationToken(meta.installationId());

            // 3. Fetch reviews from GitHub API (handling pagination)
            List<GitHubReviewFetcher.GitHubReviewDto> reviews = reviewFetcher.fetchReviews(
                meta.orgName(), meta.repoName(), meta.prNumber(), token
            );

            logger.info("reviews.fetched pullRequestId={} count={}", pullRequestId, reviews.size());

            List<Long> synchronizedReviewerIds = new ArrayList<>();

            // 4. Ingest and upsert reviewer developers and their reviews
            for (GitHubReviewFetcher.GitHubReviewDto dto : reviews) {
                if (dto.user() == null) {
                    continue;
                }

                // A. Persist/upsert reviewer developer if missing (conflict-safe)
                Long reviewerDeveloperId = upsertReviewerDeveloper(meta.organizationId(), dto.user());
                logger.info("reviewers.persisted pullRequestId={} developerId={}", pullRequestId, reviewerDeveloperId);

                if (!synchronizedReviewerIds.contains(reviewerDeveloperId)) {
                    synchronizedReviewerIds.add(reviewerDeveloperId);
                }

                // B. Detect replay by checking if review state is changing or if review already exists
                detectReviewReplay(dto.id(), dto.state(), pullRequestId);

                // C. Persist/upsert the review (conflict-safe)
                upsertPullRequestReview(pullRequestId, reviewerDeveloperId, dto);
                logger.info("reviews.persisted pullRequestId={} reviewId={}", pullRequestId, dto.id());
            }

            // 5. Enrich reviewer expertise memory for each unique reviewer
            for (Long reviewerId : synchronizedReviewerIds) {
                expertiseEnricher.enrichDeveloperExpertise(reviewerId, meta.repositoryId(), pullRequestId);
            }

            // 6. Emit downstream orchestration event
            ReviewsSynchronizedEvent event = new ReviewsSynchronizedEvent(
                pullRequestId, meta.repositoryId(), synchronizedReviewerIds
            );
            eventPublisher.publishEvent(event);
            logger.info("event.emitted type=REVIEWS_SYNCHRONIZED pullRequestId={} repositoryId={} reviewerCount={}",
                pullRequestId, meta.repositoryId(), synchronizedReviewerIds.size());

            System.out.println("[DEBUG][ReviewSyncService] persistence.completed pullRequestId=" + pullRequestId + " reviewerCount=" + synchronizedReviewerIds.size());

        } catch (Exception e) {
            logger.error("failures review sync failed for pullRequestId={}", pullRequestId, e);
            throw new RuntimeException("Review synchronization failed for pullRequestId=" + pullRequestId, e);
        }
    }

    /**
     * Idempotently upserts developer record and returns its database ID.
     */
    private Long upsertReviewerDeveloper(Long organizationId, GitHubReviewFetcher.GitHubUserDto user) {
        String insertSql = 
            "INSERT INTO developers (" +
            "  organization_id, github_user_id, username, display_name, avatar_url, " +
            "  seniority, review_capacity, reliability_score, is_active, created_at, updated_at" +
            ") VALUES (?, ?, ?, ?, ?, 'MID', 5, 0.0, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP) " +
            "ON CONFLICT (organization_id, github_user_id) DO UPDATE SET " +
            "  username = EXCLUDED.username, " +
            "  avatar_url = EXCLUDED.avatar_url, " +
            "  updated_at = CURRENT_TIMESTAMP " +
            "RETURNING id";

        return jdbcTemplate.queryForObject(
            insertSql,
            Long.class,
            organizationId,
            user.id(),
            user.login(),
            user.login(), // Display name defaults to username
            user.avatar_url()
        );
    }

    /**
     * Checks if review exists with identical state to detect replay.
     */
    private void detectReviewReplay(Long githubReviewId, String state, Long pullRequestId) {
        String checkSql = "SELECT review_state FROM pull_request_reviews WHERE github_review_id = ?";
        List<String> states = jdbcTemplate.query(
            checkSql, 
            (rs, rowNum) -> rs.getString("review_state"), 
            githubReviewId
        );

        if (!states.isEmpty()) {
            String existingState = states.get(0);
            if (existingState.equals(state)) {
                logger.info("replay.detected pullRequestId={} reviewId={} state={}", pullRequestId, githubReviewId, state);
            }
        }
    }

    /**
     * Idempotently upserts review record.
     */
    private void upsertPullRequestReview(Long pullRequestId, Long reviewerId, GitHubReviewFetcher.GitHubReviewDto dto) {
        LocalDateTime submittedAt = LocalDateTime.parse(dto.submitted_at(), DateTimeFormatter.ISO_DATE_TIME);

        String insertSql = 
            "INSERT INTO pull_request_reviews (" +
            "  pull_request_id, reviewer_id, github_review_id, review_state, review_body, review_submitted_at, created_at, updated_at" +
            ") VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP) " +
            "ON CONFLICT (github_review_id) DO UPDATE SET " +
            "  review_state = EXCLUDED.review_state, " +
            "  review_body = EXCLUDED.review_body, " +
            "  review_submitted_at = EXCLUDED.review_submitted_at, " +
            "  updated_at = CURRENT_TIMESTAMP";

        jdbcTemplate.update(
            insertSql,
            pullRequestId,
            reviewerId,
            dto.id(),
            dto.state(),
            dto.body(),
            submittedAt
        );
    }

    private record PullRequestSyncMeta(
        Long prNumber,
        Long repoGithubId,
        String repoName,
        Long installationId,
        String orgName,
        Long repositoryId,
        Long organizationId
    ) {}
}
