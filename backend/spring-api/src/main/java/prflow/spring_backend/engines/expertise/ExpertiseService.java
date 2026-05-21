package prflow.spring_backend.engines.expertise;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prflow.spring_backend.engines.complexity.ComplexityCalculatedEvent;

/**
 * Transactional orchestrator for Expertise Engine V1.
 *
 * <p>Flow:
 * - Load PR files changed in current context
 * - For each file, aggregate historical decayed touch metrics for the author
 * - Persist updated developer file-level expertise records (replay-safe upserts)
 * - Sum accumulated expertise scores for all familiar developers across all files in this PR
 * - Emit EXPERTISE_CALCULATED event containing candidates and their scores
 */
@Service
public class ExpertiseService {

    private static final Logger logger = LoggerFactory.getLogger(
        ExpertiseService.class
    );

    private final JdbcTemplate jdbcTemplate;
    private final ExpertiseCalculator expertiseCalculator;
    private final ExpertiseConfig config;
    private final ApplicationEventPublisher eventPublisher;

    public ExpertiseService(
        JdbcTemplate jdbcTemplate,
        ExpertiseCalculator expertiseCalculator,
        ExpertiseConfig config,
        ApplicationEventPublisher eventPublisher
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.expertiseCalculator = expertiseCalculator;
        this.config = config;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public void handle(ComplexityCalculatedEvent event) {
        System.out.println(
            "[DEBUG][ExpertiseEngine] service.start pullRequestId=" +
                event.pullRequestId() +
                " repositoryId=" +
                event.repositoryId()
        );
        logger.info(
            "expertise.engine.calculation.started pullRequestId={} repositoryId={}",
            event.pullRequestId(),
            event.repositoryId()
        );

        // 1. Fetch Pull Request metadata (author and repository)
        PullRequestMeta prMeta = jdbcTemplate
            .query(
                "SELECT author_id, repository_id FROM pull_requests WHERE id = ?",
                (rs, rowNum) ->
                    new PullRequestMeta(
                        rs.getLong("author_id"),
                        rs.getLong("repository_id")
                    ),
                event.pullRequestId()
            )
            .stream()
            .findFirst()
            .orElseThrow(() ->
                new IllegalStateException(
                    "Pull request not found for expertise processing"
                )
            );

        // 2. Fetch the files changed in this PR
        List<PullRequestFileMeta> prFiles = jdbcTemplate.query(
            "SELECT file_path, scope_identifier FROM pull_request_files WHERE pull_request_id = ?",
            (rs, rowNum) ->
                new PullRequestFileMeta(
                    rs.getString("file_path"),
                    rs.getString("scope_identifier")
                ),
            event.pullRequestId()
        );

        if (prFiles.isEmpty()) {
            logger.info(
                "expertise.engine.empty.files pullRequestId={} repositoryId={} authorId={}",
                event.pullRequestId(),
                prMeta.repositoryId(),
                prMeta.authorId()
            );
        }

        // 3. Accumulate expertise metrics for each file
        for (PullRequestFileMeta file : prFiles) {
            // A. Calculate decayed file touches
            List<LocalDateTime> fileTouches = jdbcTemplate.query(
                "SELECT pr.opened_at FROM pull_requests pr " +
                    "JOIN pull_request_files prf ON pr.id = prf.pull_request_id " +
                    "WHERE pr.repository_id = ? AND pr.author_id = ? AND prf.file_path = ?",
                (rs, rowNum) -> rs.getTimestamp("opened_at").toLocalDateTime(),
                prMeta.repositoryId(),
                prMeta.authorId(),
                file.filePath()
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

            // B. Calculate decayed scope touches
            List<LocalDateTime> scopeTouches = jdbcTemplate.query(
                "SELECT pr.opened_at FROM pull_requests pr " +
                    "JOIN pull_request_files prf ON pr.id = prf.pull_request_id " +
                    "WHERE pr.repository_id = ? AND pr.author_id = ? AND prf.scope_identifier = ?",
                (rs, rowNum) -> rs.getTimestamp("opened_at").toLocalDateTime(),
                prMeta.repositoryId(),
                prMeta.authorId(),
                file.scopeIdentifier()
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

            double averageRecencyWeight =
                (avgFileRecency + avgScopeRecency) / 2.0;

            // C. Calculate final score via ExpertiseCalculator
            ExpertiseBreakdown breakdown = expertiseCalculator.calculate(
                fileTouchScore,
                scopeTouchScore,
                averageRecencyWeight
            );

            System.out.println(
                "[DEBUG][ExpertiseEngine] scoring.breakdown filePath=" +
                    file.filePath() +
                    " fileTouchScore=" +
                    breakdown.fileTouchScore() +
                    " scopeTouchScore=" +
                    breakdown.scopeTouchScore() +
                    " averageRecencyWeight=" +
                    breakdown.recencyWeight() +
                    " finalScore=" +
                    breakdown.finalExpertiseScore()
            );

            logger.info(
                "expertise.engine.calculated pullRequestId={} repositoryId={} developerId={} filePath={} fileTouchScore={} scopeTouchScore={} recencyWeight={} finalScore={}",
                event.pullRequestId(),
                prMeta.repositoryId(),
                prMeta.authorId(),
                file.filePath(),
                breakdown.fileTouchScore(),
                breakdown.scopeTouchScore(),
                breakdown.recencyWeight(),
                breakdown.finalExpertiseScore()
            );

            // D. Persist snapshot (replay-safe upsert)
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
                prMeta.authorId(),
                prMeta.repositoryId(),
                file.filePath(),
                file.scopeIdentifier(),
                breakdown.finalExpertiseScore()
            );
        }

        // 4. Sum cumulative reviewer familiarity candidate scores across all files in this PR
        List<String> filePaths = prFiles
            .stream()
            .map(PullRequestFileMeta::filePath)
            .distinct()
            .toList();

        List<Long> expertiseCandidates = new ArrayList<>();
        List<Double> expertiseScores = new ArrayList<>();

        if (!filePaths.isEmpty()) {
            String inClause = filePaths
                .stream()
                .map(f -> "?")
                .collect(Collectors.joining(","));
            String sqlCandidates = String.format(
                "SELECT developer_id, SUM(expertise_score) AS cumulative_score " +
                    "FROM developer_file_expertise " +
                    "WHERE repository_id = ? AND file_path IN (%s) " +
                    "GROUP BY developer_id " +
                    "ORDER BY cumulative_score DESC",
                inClause
            );

            List<Object> queryArgs = new ArrayList<>();
            queryArgs.add(prMeta.repositoryId());
            queryArgs.addAll(filePaths);

            jdbcTemplate.query(
                sqlCandidates,
                (rs, rowNum) -> {
                    expertiseCandidates.add(rs.getLong("developer_id"));
                    expertiseScores.add(rs.getDouble("cumulative_score"));
                    return null;
                },
                queryArgs.toArray()
            );
        }

        // 5. Emit E1 orchestration event
        ExpertiseCalculatedEvent expertiseCalculatedEvent =
            new ExpertiseCalculatedEvent(
                event.pullRequestId(),
                prMeta.repositoryId(),
                expertiseCandidates,
                expertiseScores
            );
        eventPublisher.publishEvent(expertiseCalculatedEvent);

        System.out.println(
            "[DEBUG][ExpertiseEngine] persistence.completed pullRequestId=" +
                event.pullRequestId() +
                " candidateCount=" +
                expertiseCandidates.size()
        );

        logger.info(
            "expertise.engine.persistence.completed pullRequestId={} repositoryId={} candidateCount={}",
            event.pullRequestId(),
            prMeta.repositoryId(),
            expertiseCandidates.size()
        );

        System.out.println(
            "[DEBUG][ExpertiseEngine] event.emitted type=EXPERTISE_CALCULATED pullRequestId=" +
                event.pullRequestId() +
                " repositoryId=" +
                prMeta.repositoryId()
        );
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

    private record PullRequestMeta(Long authorId, Long repositoryId) {}

    private record PullRequestFileMeta(
        String filePath,
        String scopeIdentifier
    ) {}
}
