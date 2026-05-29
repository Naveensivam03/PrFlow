package prflow.spring_backend.engines.complexity.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prflow.spring_backend.engines.complexity.PullRequestAnalyzedHandler.PullRequestAnalyzedEvent;

/**
 * Transactional orchestrator for Complexity Engine V1.
 *
 * <p>Flow:
 * - lock PR row for replay-safe mutation
 * - aggregate deterministic signals from persisted file changes
 * - calculate explainable complexity breakdown
 * - persist complexity intelligence snapshot
 * - emit COMPLEXITY_CALCULATED for chained enrichment
 */
@Service
public class ComplexityService {

    private static final Logger logger = LoggerFactory.getLogger(ComplexityService.class);

    private static final String SQL_LOCK_PULL_REQUEST = """
        SELECT complexity_score, complexity_level, complexity_calculated_at
        FROM pull_requests
        WHERE id = ? AND repository_id = ?
        FOR UPDATE
        """;

    private static final String SQL_AGGREGATE_SIGNALS = """
        SELECT
            COUNT(*) AS total_files_changed,
            COALESCE(SUM(lines_added), 0) AS total_additions,
            COALESCE(SUM(lines_deleted), 0) AS total_deletions,
            COUNT(DISTINCT CASE
                WHEN POSITION('/' IN file_path) > 0 THEN regexp_replace(file_path, '/[^/]+$', '')
                ELSE '.'
            END) AS unique_directories_touched
        FROM pull_request_files
        WHERE pull_request_id = ?
        """;

    private static final String SQL_PERSIST_COMPLEXITY = """
        UPDATE pull_requests
        SET complexity_score = ?,
            complexity_level = ?,
            complexity_calculated_at = ?,
            updated_at = CURRENT_TIMESTAMP
        WHERE id = ? AND repository_id = ?
        """;

    private final JdbcTemplate jdbcTemplate;
    private final ComplexityCalculator complexityCalculator;
    private final ApplicationEventPublisher eventPublisher;

    public ComplexityService(
        JdbcTemplate jdbcTemplate,
        ComplexityCalculator complexityCalculator,
        ApplicationEventPublisher eventPublisher
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.complexityCalculator = complexityCalculator;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public void handle(PullRequestAnalyzedEvent event) {
        // TEMP DEBUG: remove after live PR verification.
        System.out.println("[DEBUG][ComplexityEngine] service.start pullRequestId=" + event.pullRequestId()
            + " repositoryId=" + event.repositoryId());
        PullRequestState state = jdbcTemplate.query(SQL_LOCK_PULL_REQUEST, new PullRequestStateMapper(), event.pullRequestId(), event.repositoryId())
            .stream()
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Pull request not found for complexity processing"));

        // Replay-safe idempotency guard: process only once per PR snapshot.
        if (state.complexityCalculatedAt() != null) {
            System.out.println("[DEBUG][ComplexityEngine] replay.duplicate pullRequestId=" + event.pullRequestId()
                + " repositoryId=" + event.repositoryId());
            logger.info("complexity.engine.replay.duplicate pullRequestId={} repositoryId={} complexityScore={} complexityLevel={} calculatedAt={}",
                event.pullRequestId(), event.repositoryId(), state.complexityScore(), state.complexityLevel(), state.complexityCalculatedAt());
            return;
        }

        logger.info("complexity.engine.normalization.started pullRequestId={} repositoryId={}", event.pullRequestId(), event.repositoryId());

        SignalAggregate signalAggregate = jdbcTemplate.query(SQL_AGGREGATE_SIGNALS, new SignalAggregateMapper(), event.pullRequestId())
            .stream()
            .findFirst()
            .orElseGet(SignalAggregate::zero);

        ComplexityBreakdown breakdown = complexityCalculator.calculate(
            signalAggregate.totalFilesChanged(),
            signalAggregate.totalAdditions(),
            signalAggregate.totalDeletions(),
            signalAggregate.uniqueDirectoriesTouched()
        );
        System.out.println("[DEBUG][ComplexityEngine] scoring.breakdown pullRequestId=" + event.pullRequestId()
            + " fileCountScore=" + breakdown.fileCountScore()
            + " additionsScore=" + breakdown.additionsScore()
            + " deletionsScore=" + breakdown.deletionsScore()
            + " diffScore=" + breakdown.diffScore()
            + " directorySpreadScore=" + breakdown.directorySpreadScore()
            + " finalScore=" + breakdown.finalScore()
            + " level=" + breakdown.complexityLevel());

        logger.info("complexity.engine.scoring.breakdown pullRequestId={} repositoryId={} fileCountScore={} additionsScore={} deletionsScore={} diffScore={} directorySpreadScore={} cycloPlaceholderScore={} coveragePlaceholderScore={}",
            event.pullRequestId(), event.repositoryId(),
            breakdown.fileCountScore(), breakdown.additionsScore(), breakdown.deletionsScore(), breakdown.diffScore(),
            breakdown.directorySpreadScore(), breakdown.cycloPlaceholderScore(), breakdown.coveragePlaceholderScore());

        logger.info("complexity.engine.final.score.calculated pullRequestId={} repositoryId={} finalScore={} complexityLevel={}",
            event.pullRequestId(), event.repositoryId(), breakdown.finalScore(), breakdown.complexityLevel());

        LocalDateTime calculatedAt = LocalDateTime.now();
        int updatedRows = jdbcTemplate.update(
            SQL_PERSIST_COMPLEXITY,
            breakdown.finalScore(),
            breakdown.complexityLevel().name(),
            calculatedAt,
            event.pullRequestId(),
            event.repositoryId()
        );

        // Exactly one row must mutate to preserve deterministic workflow behavior.
        if (updatedRows != 1) {
            throw new IllegalStateException("Complexity persistence failed due to missing or conflicting pull request row");
        }

        logger.info("complexity.engine.persistence.completed pullRequestId={} repositoryId={} complexityScore={} complexityLevel={} calculatedAt={}",
            event.pullRequestId(), event.repositoryId(), breakdown.finalScore(), breakdown.complexityLevel(), calculatedAt);
        System.out.println("[DEBUG][ComplexityEngine] persistence.completed pullRequestId=" + event.pullRequestId()
            + " repositoryId=" + event.repositoryId()
            + " complexityScore=" + breakdown.finalScore()
            + " complexityLevel=" + breakdown.complexityLevel());

        ComplexityCalculatedEvent complexityCalculatedEvent = new ComplexityCalculatedEvent(
            event.pullRequestId(),
            event.repositoryId(),
            breakdown.finalScore(),
            breakdown.complexityLevel()
        );
        eventPublisher.publishEvent(complexityCalculatedEvent);
        System.out.println("[DEBUG][ComplexityEngine] event.emitted type=COMPLEXITY_CALCULATED pullRequestId="
            + event.pullRequestId() + " repositoryId=" + event.repositoryId());

        logger.info("complexity.engine.event.emitted eventType=COMPLEXITY_CALCULATED pullRequestId={} repositoryId={} complexityScore={} complexityLevel={}",
            event.pullRequestId(), event.repositoryId(), breakdown.finalScore(), breakdown.complexityLevel());
    }

    /**
     * Minimal PR state needed to evaluate idempotent replay behavior.
     */
    private record PullRequestState(Double complexityScore, String complexityLevel, LocalDateTime complexityCalculatedAt) {}

    private static class PullRequestStateMapper implements RowMapper<PullRequestState> {
        @Override
        public PullRequestState mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new PullRequestState(
                rs.getObject("complexity_score") != null ? rs.getDouble("complexity_score") : null,
                rs.getString("complexity_level"),
                rs.getTimestamp("complexity_calculated_at") != null
                    ? rs.getTimestamp("complexity_calculated_at").toLocalDateTime()
                    : null
            );
        }
    }

    /**
     * Aggregated V1 signal set derived from pull_request_files.
     */
    private record SignalAggregate(int totalFilesChanged, int totalAdditions, int totalDeletions, int uniqueDirectoriesTouched) {
        static SignalAggregate zero() {
            return new SignalAggregate(0, 0, 0, 0);
        }
    }

    private static class SignalAggregateMapper implements RowMapper<SignalAggregate> {
        @Override
        public SignalAggregate mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new SignalAggregate(
                rs.getInt("total_files_changed"),
                rs.getInt("total_additions"),
                rs.getInt("total_deletions"),
                rs.getInt("unique_directories_touched")
            );
        }
    }
}
