package prflow.spring_backend.engines.complexity.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
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

    // Logger removed: logging statements converted to comments to keep behavior deterministic.

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
        // temp debug output removed
        PullRequestState state = jdbcTemplate
            .query(
                SQL_LOCK_PULL_REQUEST,
                new PullRequestStateMapper(),
                event.pullRequestId(),
                event.repositoryId()
            )
            .stream()
            .findFirst()
            .orElseThrow(() ->
                new IllegalStateException(
                    "Pull request not found for complexity processing"
                )
            );

        // Replay-safe idempotency guard: process only once per PR snapshot.
        if (state.complexityCalculatedAt() != null) {
            // replay duplicate debug removed
            // log removed: complexity.engine.replay.duplicate (see state variables)
            return;
        }

        // log removed: complexity.engine.normalization.started (was emitted before aggregation)

        SignalAggregate signalAggregate = jdbcTemplate
            .query(
                SQL_AGGREGATE_SIGNALS,
                new SignalAggregateMapper(),
                event.pullRequestId()
            )
            .stream()
            .findFirst()
            .orElseGet(SignalAggregate::zero);

        ComplexityBreakdown breakdown = complexityCalculator.calculate(
            signalAggregate.totalFilesChanged(),
            signalAggregate.totalAdditions(),
            signalAggregate.totalDeletions(),
            signalAggregate.uniqueDirectoriesTouched()
        );


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
            throw new IllegalStateException(
                "Complexity persistence failed due to missing or conflicting pull request row"
            );
        }

        // log removed: complexity.engine.persistence.completed and debug persistence output removed
// it gets mapped into the event/complexityCalculatedEvent.java
        ComplexityCalculatedEvent complexityCalculatedEvent =
            new ComplexityCalculatedEvent(
                event.pullRequestId(),
                event.repositoryId(),
                breakdown.finalScore(),
                breakdown.complexityLevel()
            );
        eventPublisher.publishEvent(complexityCalculatedEvent);
        // log removed: event emitted COMPLEXITY_CALCULATED (event published via ApplicationEventPublisher)
    }

    /**
     * Minimal PR state needed to evaluate idempotent replay behavior.
     */
    private record PullRequestState(
        Double complexityScore,
        String complexityLevel,
        LocalDateTime complexityCalculatedAt
    ) {}

    private static class PullRequestStateMapper
        implements RowMapper<PullRequestState>
    {

        @Override
        public PullRequestState mapRow(ResultSet rs, int rowNum)
            throws SQLException {
            return new PullRequestState(
                rs.getObject("complexity_score") != null
                    ? rs.getDouble("complexity_score")
                    : null,
                rs.getString("complexity_level"),
                rs.getTimestamp("complexity_calculated_at") != null
                    ? rs
                          .getTimestamp("complexity_calculated_at")
                          .toLocalDateTime()
                    : null
            );
        }
    }

    /**
     * Aggregated V1 signal set derived from pull_request_files.
     */
    private record SignalAggregate(
        int totalFilesChanged,
        int totalAdditions,
        int totalDeletions,
        int uniqueDirectoriesTouched
    ) {
        static SignalAggregate zero() {
            return new SignalAggregate(0, 0, 0, 0);
        }
    }

    private static class SignalAggregateMapper
        implements RowMapper<SignalAggregate>
    {

        @Override
        public SignalAggregate mapRow(ResultSet rs, int rowNum)
            throws SQLException {
            return new SignalAggregate(
                rs.getInt("total_files_changed"),
                rs.getInt("total_additions"),
                rs.getInt("total_deletions"),
                rs.getInt("unique_directories_touched")
            );
        }
    }
}
