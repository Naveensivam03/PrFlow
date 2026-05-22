package prflow.spring_backend.engines.assignment;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prflow.spring_backend.engines.expertise.ExpertiseCalculatedEvent;
import prflow.spring_backend.engines.assignment.model.ReviewerCandidate;
import prflow.spring_backend.enums.DeveloperSeniority;

/**
 * Core transactional engine responsible for routing, scoring, and matching reviewer assignments.
 */
@Service
public class AssignmentService {

    private static final Logger logger = LoggerFactory.getLogger(AssignmentService.class);
    private final Random random = new Random();

    private final JdbcTemplate jdbcTemplate;
    private final AssignmentScoringService scoringService;
    private final AssignmentConfig config;
    private final ApplicationEventPublisher eventPublisher;

    public AssignmentService(
        JdbcTemplate jdbcTemplate,
        AssignmentScoringService scoringService,
        AssignmentConfig config,
        ApplicationEventPublisher eventPublisher
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.scoringService = scoringService;
        this.config = config;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Executes the V1 Reviewer Assignment pipeline.
     */
    @Transactional
    public void assignReviewers(ExpertiseCalculatedEvent event) {
        assignReviewers(event, Collections.emptyList());
    }

    /**
     * Overloaded Reviewer Assignment pipeline supporting explicit developer exclusions for reassignment.
     */
    @Transactional
    public void assignReviewers(ExpertiseCalculatedEvent event, List<Long> excludedDeveloperIds) {
        Long pullRequestId = event.pullRequestId();
        Long repositoryId = event.repositoryId();

        System.out.println("[DEBUG][AssignmentEngine] assignment.started pullRequestId=" + pullRequestId);
        logger.info("assignment.started pullRequestId={} repositoryId={} exclusions={}", pullRequestId, repositoryId, excludedDeveloperIds);

        try {
            // 1. Fetch Pull Request metadata
            PullRequestState prState = jdbcTemplate.query(
                "SELECT author_id, repository_id, complexity_score FROM pull_requests WHERE id = ?",
                (rs, rowNum) -> new PullRequestState(
                    rs.getLong("author_id"),
                    rs.getLong("repository_id"),
                    rs.getObject("complexity_score") != null ? rs.getDouble("complexity_score") : null
                ),
                pullRequestId
            ).stream().findFirst().orElseThrow(() -> new IllegalStateException("Pull request not found"));

            Long authorId = prState.authorId();
            Double complexityScore = prState.complexityScore() != null ? prState.complexityScore() : 0.0;

            // 2. Load all active repository developers
            List<DeveloperMeta> allContributors = jdbcTemplate.query(
                "SELECT d.id, d.username, d.seniority, rd.contribution_count " +
                "FROM repository_developers rd " +
                "JOIN developers d ON rd.developer_id = d.id " +
                "WHERE rd.repository_id = ? AND d.is_active = TRUE",
                (rs, rowNum) -> new DeveloperMeta(
                    rs.getLong("id"),
                    rs.getString("username"),
                    DeveloperSeniority.valueOf(rs.getString("seniority")),
                    rs.getInt("contribution_count")
                ),
                repositoryId
            );

            System.out.println("[DEBUG][AssignmentEngine] candidates.loaded pullRequestId=" + pullRequestId 
                + " count=" + allContributors.size());
            logger.info("candidates.loaded pullRequestId={} repositoryId={} candidateCount={}", 
                pullRequestId, repositoryId, allContributors.size());

            // Query active reviewer assignments on this PR to exclude them and calculate target slots
            List<Long> activeReviewerIds = jdbcTemplate.query(
                "SELECT developer_id FROM reviewer_assignments WHERE pull_request_id = ? AND assignment_status IN ('ASSIGNED', 'REMINDER_SENT', 'STALE')",
                (rs, rowNum) -> rs.getLong("developer_id"),
                pullRequestId
            );

            // 3. Exclude PR author, explicitly excluded developers, and active reviewers from candidates pool
            List<DeveloperMeta> poolWithoutAuthor = allContributors.stream()
                .filter(c -> !c.id().equals(authorId))
                .filter(c -> !excludedDeveloperIds.contains(c.id()))
                .filter(c -> !activeReviewerIds.contains(c.id()))
                .collect(Collectors.toList());

            System.out.println("[DEBUG][AssignmentEngine] candidates.filtered pullRequestId=" + pullRequestId 
                + " poolWithoutAuthor=" + poolWithoutAuthor.size());
            logger.info("candidates.filtered pullRequestId={} repositoryId={} poolWithoutAuthorCount={}", 
                pullRequestId, repositoryId, poolWithoutAuthor.size());

            // Map expertise scores from event
            Map<Long, Double> expertiseMap = new HashMap<>();
            for (int i = 0; i < event.expertiseCandidates().size(); i++) {
                expertiseMap.put(event.expertiseCandidates().get(i), event.expertiseScores().get(i));
            }

            // 4. Apply Complexity Gating
            boolean isHighComplexity = complexityScore > config.getComplexityThreshold();
            List<DeveloperMeta> gatedPool;
            if (isHighComplexity) {
                gatedPool = poolWithoutAuthor.stream()
                    .filter(c -> c.seniority() == DeveloperSeniority.SENIOR)
                    .collect(Collectors.toList());
                System.out.println("[DEBUG][AssignmentEngine] complexity.gate.applied pullRequestId=" + pullRequestId 
                    + " highComplexity=true gatedPoolSize=" + gatedPool.size());
                logger.info("complexity.gate.applied pullRequestId={} repositoryId={} complexityScore={} highComplexity=true gatedPoolSize={}", 
                    pullRequestId, repositoryId, complexityScore, gatedPool.size());
            } else {
                gatedPool = new ArrayList<>(poolWithoutAuthor);
                System.out.println("[DEBUG][AssignmentEngine] complexity.gate.applied pullRequestId=" + pullRequestId 
                    + " highComplexity=false gatedPoolSize=" + gatedPool.size());
                logger.info("complexity.gate.applied pullRequestId={} repositoryId={} complexityScore={} highComplexity=false gatedPoolSize={}", 
                    pullRequestId, repositoryId, complexityScore, gatedPool.size());
            }

            List<ReviewerCandidate> assignedCandidates = new ArrayList<>();
            int targetLimit = config.getDefaultReviewerLimit();
            int remainingLimit = targetLimit - activeReviewerIds.size();

            if (remainingLimit <= 0) {
                System.out.println("[DEBUG][AssignmentEngine] assignments.skipped pullRequestId=" + pullRequestId 
                    + " activeReviewers=" + activeReviewerIds.size() + " >= targetLimit=" + targetLimit);
                logger.info("assignments.skipped pullRequestId={} repositoryId={} activeCount={}", 
                    pullRequestId, repositoryId, activeReviewerIds.size());
                return;
            }

            // 5. Junior Growth Routing (only for non-high complexity)
            if (!isHighComplexity && config.getJuniorGrowthRatio() > 0.0 && assignedCandidates.size() < remainingLimit) {
                double roll = random.nextDouble();
                boolean tryJuniorGrowth = roll <= config.getJuniorGrowthRatio();
                System.out.println("[DEBUG][AssignmentEngine] junior.growth.evaluation pullRequestId=" + pullRequestId 
                    + " roll=" + String.format("%.4f", roll) + " threshold=" + config.getJuniorGrowthRatio() + " trigger=" + tryJuniorGrowth);
                
                if (tryJuniorGrowth) {
                    // Find qualified JUNIOR candidate with highest expertise (or highest repository contributions if expertise is same)
                    Optional<DeveloperMeta> selectedJunior = gatedPool.stream()
                        .filter(c -> c.seniority() == DeveloperSeniority.JUNIOR)
                        .max(Comparator.comparingDouble((DeveloperMeta d) -> expertiseMap.getOrDefault(d.id(), 0.0))
                            .thenComparingInt(DeveloperMeta::contributionCount));

                    if (selectedJunior.isPresent()) {
                        DeveloperMeta jr = selectedJunior.get();
                        int activeCount = countActiveReviews(jr.id());
                        double expertise = expertiseMap.getOrDefault(jr.id(), 0.0);
                        double score = scoringService.calculateScore(expertise, activeCount);
                        String reason = scoringService.buildReason(expertise, activeCount, score) + " [Junior Growth Triggered]";
                        
                        ReviewerCandidate jrCandidate = new ReviewerCandidate(
                            jr.id(), jr.username(), jr.seniority(), expertise, activeCount, score, reason, "JUNIOR_GROWTH"
                        );
                        assignedCandidates.add(jrCandidate);

                        // Remove from gated pool to avoid double-assignment
                        gatedPool.remove(jr);
                        
                        System.out.println("[DEBUG][AssignmentEngine] junior.growth.assigned pullRequestId=" + pullRequestId 
                            + " developerId=" + jr.id() + " username=" + jr.username());
                        logger.info("junior.growth.assigned pullRequestId={} developerId={} username={}", 
                            pullRequestId, jr.id(), jr.username());
                    }
                }
            }

            // 6. Score & Rank standard candidates
            List<ReviewerCandidate> scoredCandidates = new ArrayList<>();
            for (DeveloperMeta dev : gatedPool) {
                double expertise = expertiseMap.getOrDefault(dev.id(), 0.0);
                if (expertise <= 0.0) {
                    continue;
                }
                int activeCount = countActiveReviews(dev.id());
                double finalScore = scoringService.calculateScore(expertise, activeCount);
                String reason = scoringService.buildReason(expertise, activeCount, finalScore);
                
                ReviewerCandidate candidate = new ReviewerCandidate(
                    dev.id(), dev.username(), dev.seniority(), expertise, activeCount, finalScore, reason, "STANDARD"
                );
                scoredCandidates.add(candidate);
                
                System.out.println("[DEBUG][AssignmentEngine] candidate.scored pullRequestId=" + pullRequestId 
                    + " developerId=" + dev.id() + " username=" + dev.username() + " expertise=" + expertise 
                    + " activeReviews=" + activeCount + " finalScore=" + finalScore);
                logger.info("candidate.scored pullRequestId={} developerId={} username={} expertiseScore={} activeReviewsCount={} finalScore={}",
                    pullRequestId, dev.id(), dev.username(), expertise, activeCount, finalScore);
            }

            // Sort by score descending
            scoredCandidates.sort(Comparator.comparingDouble(ReviewerCandidate::finalScore).reversed());

            // Select remaining top-ranked candidates to fulfill reviewer limit
            int remainingNeeded = remainingLimit - assignedCandidates.size();
            for (int i = 0; i < Math.min(remainingNeeded, scoredCandidates.size()); i++) {
                assignedCandidates.add(scoredCandidates.get(i));
            }

            System.out.println("[DEBUG][AssignmentEngine] reviewers.selected pullRequestId=" + pullRequestId 
                + " selectedCount=" + assignedCandidates.size() + " remainingLimit=" + remainingLimit);
            logger.info("reviewers.selected pullRequestId={} repositoryId={} selectedCount={} target={}", 
                pullRequestId, repositoryId, assignedCandidates.size(), remainingLimit);

            // 7. Fallback Logic: if we are under-capacity, loop through fallbacks sequentially
            if (assignedCandidates.size() < remainingLimit) {
                Set<Long> alreadyAssignedIds = assignedCandidates.stream()
                    .map(ReviewerCandidate::developerId)
                    .collect(Collectors.toSet());

                // Fallback 1: Highest expertise candidate in the repository (excluding author & assigned)
                if (assignedCandidates.size() < remainingLimit) {
                    Optional<DeveloperMeta> fb1 = poolWithoutAuthor.stream()
                        .filter(c -> !alreadyAssignedIds.contains(c.id()))
                        .filter(c -> expertiseMap.getOrDefault(c.id(), 0.0) > 0.0)
                        .max(Comparator.comparingDouble(c -> expertiseMap.getOrDefault(c.id(), 0.0)));

                    if (fb1.isPresent()) {
                        DeveloperMeta dev = fb1.get();
                        double expertise = expertiseMap.get(dev.id());
                        String reason = String.format("Fallback Level 1: Highest Expertise Candidate (Expertise score: %.2f)", expertise);
                        ReviewerCandidate candidate = new ReviewerCandidate(
                            dev.id(), dev.username(), dev.seniority(), expertise, 0, expertise, reason, "FALLBACK"
                        );
                        assignedCandidates.add(candidate);
                        alreadyAssignedIds.add(dev.id());
                        System.out.println("[DEBUG][AssignmentEngine] fallback.used pullRequestId=" + pullRequestId 
                            + " level=1 developerId=" + dev.id() + " username=" + dev.username());
                        logger.info("fallback.used pullRequestId={} level=1 developerId={} username={}", pullRequestId, dev.id(), dev.username());
                    }
                }

                // Fallback 2: Any Senior contributor in the repository (excluding author & assigned)
                if (assignedCandidates.size() < remainingLimit) {
                    Optional<DeveloperMeta> fb2 = poolWithoutAuthor.stream()
                        .filter(c -> !alreadyAssignedIds.contains(c.id()))
                        .filter(c -> c.seniority() == DeveloperSeniority.SENIOR)
                        .findFirst();

                    if (fb2.isPresent()) {
                        DeveloperMeta dev = fb2.get();
                        String reason = "Fallback Level 2: Senior Contributor routing";
                        ReviewerCandidate candidate = new ReviewerCandidate(
                            dev.id(), dev.username(), dev.seniority(), 0.0, 0, 0.0, reason, "FALLBACK"
                        );
                        assignedCandidates.add(candidate);
                        alreadyAssignedIds.add(dev.id());
                        System.out.println("[DEBUG][AssignmentEngine] fallback.used pullRequestId=" + pullRequestId 
                            + " level=2 developerId=" + dev.id() + " username=" + dev.username());
                        logger.info("fallback.used pullRequestId={} level=2 developerId={} username={}", pullRequestId, dev.id(), dev.username());
                    }
                }

                // Fallback 3: Repository contributor with the highest contribution_count (excluding author & assigned)
                if (assignedCandidates.size() < remainingLimit) {
                    Optional<DeveloperMeta> fb3 = poolWithoutAuthor.stream()
                        .filter(c -> !alreadyAssignedIds.contains(c.id()))
                        .max(Comparator.comparingInt(DeveloperMeta::contributionCount));

                    if (fb3.isPresent()) {
                        DeveloperMeta dev = fb3.get();
                        String reason = String.format("Fallback Level 3: Most active contributor (Contributions: %d)", dev.contributionCount());
                        ReviewerCandidate candidate = new ReviewerCandidate(
                            dev.id(), dev.username(), dev.seniority(), 0.0, 0, 0.0, reason, "FALLBACK"
                        );
                        assignedCandidates.add(candidate);
                        alreadyAssignedIds.add(dev.id());
                        System.out.println("[DEBUG][AssignmentEngine] fallback.used pullRequestId=" + pullRequestId 
                            + " level=3 developerId=" + dev.id() + " username=" + dev.username());
                        logger.info("fallback.used pullRequestId={} level=3 developerId={} username={}", pullRequestId, dev.id(), dev.username());
                    }
                }
            }

            // 8. Persist Reviewer Recommendations (Idempotent Upsert)
            List<Long> assignedIds = new ArrayList<>();
            for (ReviewerCandidate rc : assignedCandidates) {
                jdbcTemplate.update(
                    "INSERT INTO reviewer_assignments (" +
                    "  pull_request_id, developer_id, assignment_score, assignment_reason, assignment_type, " +
                    "  assignment_status, escalation_level, created_at, updated_at" +
                    ") VALUES (?, ?, ?, ?, ?, 'ASSIGNED', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP) " +
                    "ON CONFLICT (pull_request_id, developer_id) DO UPDATE SET " +
                    "  assignment_score = EXCLUDED.assignment_score, " +
                    "  assignment_reason = EXCLUDED.assignment_reason, " +
                    "  assignment_type = EXCLUDED.assignment_type, " +
                    "  assignment_status = 'ASSIGNED', " +
                    "  escalation_level = 0, " +
                    "  reminder_sent_at = NULL, " +
                    "  escalated_at = NULL, " +
                    "  reassigned_at = NULL, " +
                    "  updated_at = CURRENT_TIMESTAMP",
                    pullRequestId,
                    rc.developerId(),
                    rc.finalScore(),
                    rc.reason(),
                    rc.assignmentType()
                );
                assignedIds.add(rc.developerId());
            }

            System.out.println("[DEBUG][AssignmentEngine] assignments.persisted pullRequestId=" + pullRequestId 
                + " count=" + assignedIds.size());
            logger.info("assignments.persisted pullRequestId={} repositoryId={} count={}", 
                pullRequestId, repositoryId, assignedIds.size());

            // 9. Emit REVIEWERS_ASSIGNED
            ReviewersAssignedEvent assignedEvent = new ReviewersAssignedEvent(
                pullRequestId,
                repositoryId,
                assignedIds
            );
            eventPublisher.publishEvent(assignedEvent);

            System.out.println("[DEBUG][AssignmentEngine] event.emitted type=REVIEWERS_ASSIGNED pullRequestId=" + pullRequestId);
            logger.info("event.emitted eventType=REVIEWERS_ASSIGNED pullRequestId={} repositoryId={}", pullRequestId, repositoryId);

        } catch (Exception e) {
            System.err.println("[DEBUG][AssignmentEngine] failures pullRequestId=" + pullRequestId + " error=" + e.getMessage());
            logger.error("failures pullRequestId={} error={}", pullRequestId, e.getMessage(), e);
            throw e;
        }
    }

    private int countActiveReviews(Long developerId) {
        Integer activeCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) " +
            "FROM reviewer_assignments ra " +
            "JOIN pull_requests pr ON ra.pull_request_id = pr.id " +
            "WHERE ra.developer_id = ? AND pr.status IN ('OPEN', 'DRAFT') " +
            "  AND ra.assignment_status IN ('ASSIGNED', 'REMINDER_SENT', 'STALE')",
            Integer.class,
            developerId
        );
        return activeCount != null ? activeCount : 0;
    }

    private record PullRequestState(Long authorId, Long repositoryId, Double complexityScore) {}

    private record DeveloperMeta(
        Long id,
        String username,
        DeveloperSeniority seniority,
        int contributionCount
    ) {}
}
