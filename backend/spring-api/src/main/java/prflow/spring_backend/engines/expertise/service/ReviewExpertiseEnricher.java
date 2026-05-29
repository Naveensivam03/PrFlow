package prflow.spring_backend.engines.expertise.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewExpertiseEnricher {

    private static final Logger logger = LoggerFactory.getLogger(
        ReviewExpertiseEnricher.class
    );

    private final JdbcTemplate jdbcTemplate;
    private final ExpertiseCalculator expertiseCalculator;
    private final ExpertiseConfig config;

    public ReviewExpertiseEnricher(
        JdbcTemplate jdbcTemplate,
        ExpertiseCalculator expertiseCalculator,
        ExpertiseConfig config
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.expertiseCalculator = expertiseCalculator;
        this.config = config;
    }

    /**
     * Recalculates and enriches the expertise score of a developer for files changed in a PR.
     * Consolidates both authored touch score and review participation/approval scores.
     * This is 100% idempotent and replay-safe.
     */
    @Transactional
    public void enrichDeveloperExpertise(
        Long developerId,
        Long repositoryId,
        Long pullRequestId
    ) {
        logger.info(
            "expertise.enrichment.started pullRequestId={} repositoryId={} developerId={}",
            pullRequestId,
            repositoryId,
            developerId
        );

        // 1. Fetch the files changed in this PR
        List<PullRequestFileMeta> prFiles = jdbcTemplate.query(
            "SELECT file_path, scope_identifier FROM pull_request_files WHERE pull_request_id = ?",
            (rs, rowNum) ->
                new PullRequestFileMeta(
                    rs.getString("file_path"),
                    rs.getString("scope_identifier")
                ),
            pullRequestId
        );

        if (prFiles.isEmpty()) {
            logger.warn(
                "expertise.enrichment.no_files pullRequestId={} repositoryId={} developerId={}",
                pullRequestId,
                repositoryId,
                developerId
            );
            return;
        }

        // 2. For each file, calculate touch and review scores from scratch, then upsert
        for (PullRequestFileMeta file : prFiles) {
            // A. Standard touch score (where developer is the author)
            double touchScore = calculateTouchScore(
                developerId,
                repositoryId,
                file.filePath(),
                file.scopeIdentifier()
            );

            // B. Review score (where developer is the reviewer)
            double reviewScore = calculateReviewScore(
                developerId,
                repositoryId,
                file.filePath()
            );

            // C. Sum them and round to 2 decimal places, bounding to 999.99
            double rawTotalScore = touchScore + reviewScore;
            double finalScore = Math.min(
                999.99,
                Math.round(rawTotalScore * 100.0) / 100.0
            );

            logger.info(
                "expertise.enriched pullRequestId={} repositoryId={} developerId={} filePath={} reviewScore={} touchScore={} finalScore={}",
                pullRequestId,
                repositoryId,
                developerId,
                file.filePath(),
                reviewScore,
                touchScore,
                finalScore
            );

            // D. Conflict-safe upsert into developer_file_expertise
            jdbcTemplate.update(
                "INSERT INTO developer_file_expertise (" +
                    "  developer_id, repository_id, file_path, scope_identifier, " +
                    "  expertise_score, last_activity_at, created_at, updated_at" +
                    ") VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP) " +
                    "ON CONFLICT (developer_id, repository_id, file_path) DO UPDATE SET " +
                    "  scope_identifier = EXCLUDED.scope_identifier, " +
                    "  expertise_score = EXCLUDED.expertise_score, " +
                    "  last_activity_at = EXCLUDED.last_activity_at, " +
                    "  updated_at = CURRENT_TIMESTAMP",
                developerId,
                repositoryId,
                file.filePath(),
                file.scopeIdentifier(),
                finalScore
            );
        }

        logger.info(
            "expertise.enrichment.completed pullRequestId={} repositoryId={} developerId={}",
            pullRequestId,
            repositoryId,
            developerId
        );
    }

    /**
     * Calculates review-based expertise using standard counts and weights:
     * - Each review contribution (participation) grants 1.0 points.
     * - Each approval grants an additional 2.0 points.
     */
    private double calculateReviewScore(
        Long developerId,
        Long repositoryId,
        String filePath
    ) {
        // Query to get review stats for this reviewer and file in this repository
        String sql =
            "SELECT " +
            "  COUNT(CASE WHEN r.review_state = 'APPROVED' THEN 1 END) as approvals_count, " +
            "  COUNT(1) as total_reviews_count " +
            "FROM pull_request_reviews r " +
            "JOIN pull_requests pr ON r.pull_request_id = pr.id " +
            "JOIN pull_request_files prf ON pr.id = prf.pull_request_id " +
            "WHERE r.reviewer_id = ? AND pr.repository_id = ? AND prf.file_path = ?";

        return jdbcTemplate.query(
            sql,
            rs -> {
                if (rs.next()) {
                    int approvals = rs.getInt("approvals_count");
                    int totalReviews = rs.getInt("total_reviews_count");
                    // 1.0 for participation + 2.0 additional for approvals
                    return (totalReviews * 1.0) + (approvals * 2.0);
                }
                return 0.0;
            },
            developerId,
            repositoryId,
            filePath
        );
    }

    /**
     * Calculates the standard touch score, matching the logic of ExpertiseService.
     */
    private double calculateTouchScore(
        Long developerId,
        Long repositoryId,
        String filePath,
        String scopeIdentifier
    ) {
        // Fetch file touches
        List<LocalDateTime> fileTouches = jdbcTemplate.query(
            "SELECT pr.opened_at FROM pull_requests pr " +
                "JOIN pull_request_files prf ON pr.id = prf.pull_request_id " +
                "WHERE pr.repository_id = ? AND pr.author_id = ? AND prf.file_path = ?",
            (rs, rowNum) -> rs.getTimestamp("opened_at").toLocalDateTime(),
            repositoryId,
            developerId,
            filePath
        );

        double fileTouchScore = 0.0;
        double totalFileWeight = 0.0;
        for (LocalDateTime openedAt : fileTouches) {
            double weight = getRecencyWeight(openedAt);
            fileTouchScore += weight;
            totalFileWeight += weight;
        }
        double avgFileRecency = fileTouches.isEmpty()
            ? 1.0
            : (totalFileWeight / fileTouches.size());

        // Fetch scope touches
        List<LocalDateTime> scopeTouches = jdbcTemplate.query(
            "SELECT pr.opened_at FROM pull_requests pr " +
                "JOIN pull_request_files prf ON pr.id = prf.pull_request_id " +
                "WHERE pr.repository_id = ? AND pr.author_id = ? AND prf.scope_identifier = ?",
            (rs, rowNum) -> rs.getTimestamp("opened_at").toLocalDateTime(),
            repositoryId,
            developerId,
            scopeIdentifier
        );

        double scopeTouchScore = 0.0;
        double totalScopeWeight = 0.0;
        for (LocalDateTime openedAt : scopeTouches) {
            double weight = getRecencyWeight(openedAt);
            scopeTouchScore += weight;
            totalScopeWeight += weight;
        }
        double avgScopeRecency = scopeTouches.isEmpty()
            ? 1.0
            : (totalScopeWeight / scopeTouches.size());

        double averageRecencyWeight = (avgFileRecency + avgScopeRecency) / 2.0;

        ExpertiseBreakdown breakdown = expertiseCalculator.calculate(
            fileTouchScore,
            scopeTouchScore,
            averageRecencyWeight
        );

        return breakdown.finalExpertiseScore();
    }

    private double getRecencyWeight(LocalDateTime openedAt) {
        long daysAgo = ChronoUnit.DAYS.between(openedAt, LocalDateTime.now());
        if (daysAgo < 30) {
            return config.getDecay().getUnder30Days();
        } else if (daysAgo <= 90) {
            return config.getDecay().getUnder90Days();
        } else if (daysAgo <= 180) {
            return config.getDecay().getUnder180Days();
        } else {
            return config.getDecay().getOlderThan180Days();
        }
    }

    private record PullRequestFileMeta(
        String filePath,
        String scopeIdentifier
    ) {}
}
