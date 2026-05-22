package prflow.spring_backend.engines.escalation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prflow.spring_backend.engines.assignment.AssignmentService;
import prflow.spring_backend.engines.expertise.ExpertiseCalculatedEvent;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Central orchestrator for the Pull Request SLA Escalation Engine (V1).
 * Runs periodically to scan pending reviewer assignments, calculate wait times,
 * and execute multi-level escalation transitions (Reminder, Stale, Reassignment)
 * in an idempotent and replay-safe manner.
 */
@Service
public class EscalationService {

    private static final Logger logger = LoggerFactory.getLogger(EscalationService.class);

    private final JdbcTemplate jdbcTemplate;
    private final AssignmentService assignmentService;
    private final EmailNotificationService emailNotificationService;
    private final ApplicationEventPublisher eventPublisher;

    public EscalationService(
        JdbcTemplate jdbcTemplate,
        AssignmentService assignmentService,
        EmailNotificationService emailNotificationService,
        ApplicationEventPublisher eventPublisher
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.assignmentService = assignmentService;
        this.emailNotificationService = emailNotificationService;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Hourly scheduler scanning all active reviews for SLA violations.
     * Scheduled to execute every hour (3600000 milliseconds).
     */
    @Scheduled(fixedRate = 3600000)
    public void scanAndEscalate() {
        logger.info("sla.escalation.scan.started");
        try {
            checkEscalations(LocalDateTime.now());
            logger.info("sla.escalation.scan.completed");
        } catch (Exception e) {
            logger.error("sla.escalation.scan.failed", e);
        }
    }

    /**
     * Checks escalations relative to a specific timestamp.
     * Exposed for deterministic testing.
     */
    public void checkEscalations(LocalDateTime now) {
        System.out.println("[DEBUG][EscalationEngine] scan.started now=" + now);

        // Query active assignments for open pull requests
        String query = 
            "SELECT " +
            "  ra.pull_request_id, " +
            "  ra.developer_id, " +
            "  ra.created_at AS assignment_created_at, " +
            "  ra.assignment_status, " +
            "  ra.escalation_level, " +
            "  d.username, " +
            "  pr.title AS pr_title, " +
            "  pr.github_pr_number, " +
            "  pr.complexity_score, " +
            "  r.name AS repo_name, " +
            "  r.id AS repository_id " +
            "FROM reviewer_assignments ra " +
            "JOIN pull_requests pr ON ra.pull_request_id = pr.id " +
            "JOIN repositories r ON pr.repository_id = r.id " +
            "JOIN developers d ON ra.developer_id = d.id " +
            "WHERE pr.status = 'OPEN' " +
            "  AND ra.assignment_status IN ('ASSIGNED', 'REMINDER_SENT', 'STALE') " +
            "ORDER BY ra.created_at ASC";

        List<AssignmentEscalationMeta> assignments = jdbcTemplate.query(
            query,
            (rs, rowNum) -> new AssignmentEscalationMeta(
                rs.getLong("pull_request_id"),
                rs.getLong("developer_id"),
                rs.getTimestamp("assignment_created_at").toLocalDateTime(),
                rs.getString("assignment_status"),
                rs.getInt("escalation_level"),
                rs.getString("username"),
                rs.getString("pr_title"),
                rs.getLong("github_pr_number"),
                rs.getObject("complexity_score") != null ? rs.getDouble("complexity_score") : null,
                rs.getString("repo_name"),
                rs.getLong("repository_id")
            )
        );

        System.out.println("[DEBUG][EscalationEngine] active.assignments.loaded count=" + assignments.size());

        for (AssignmentEscalationMeta assignment : assignments) {
            long hoursWaiting = ChronoUnit.HOURS.between(assignment.assignmentCreatedAt(), now);
            System.out.println("[DEBUG][EscalationEngine] evaluating assignment pullRequestId=" + assignment.pullRequestId() 
                + " reviewer=" + assignment.username() + " hoursWaiting=" + hoursWaiting 
                + " currentStatus=" + assignment.assignmentStatus() + " currentLevel=" + assignment.escalationLevel());

            try {
                processEscalationForAssignment(assignment, hoursWaiting, now);
            } catch (Exception e) {
                logger.error("failures escalation processing failed for pullRequestId={} developerId={}", 
                    assignment.pullRequestId(), assignment.developerId(), e);
            }
        }
    }

    /**
     * Evaluates and transactionally processes SLA state transitions for a single assignment.
     */
    @Transactional
    public void processEscalationForAssignment(AssignmentEscalationMeta assignment, long hoursWaiting, LocalDateTime now) {
        if (hoursWaiting >= 48) {
            if (assignment.escalationLevel() < 3) {
                triggerLevel3Reassignment(assignment, now);
            }
        } else if (hoursWaiting >= 36) {
            if (assignment.escalationLevel() < 2) {
                triggerLevel2Stale(assignment, now, hoursWaiting);
            }
        } else if (hoursWaiting >= 24) {
            if (assignment.escalationLevel() < 1) {
                triggerLevel1Reminder(assignment, now, hoursWaiting);
            }
        }
    }

    private void triggerLevel1Reminder(AssignmentEscalationMeta assignment, LocalDateTime now, long hoursWaiting) {
        System.out.println("[DEBUG][EscalationEngine] level1.triggered pullRequestId=" + assignment.pullRequestId() 
            + " developerId=" + assignment.developerId() + " username=" + assignment.username());
        logger.info("sla.level1.triggered pullRequestId={} developerId={} username={} hoursWaiting={}", 
            assignment.pullRequestId(), assignment.developerId(), assignment.username(), hoursWaiting);

        // 1. Transactionally update state
        int updated = jdbcTemplate.update(
            "UPDATE reviewer_assignments SET " +
            "  assignment_status = 'REMINDER_SENT', " +
            "  reminder_sent_at = ?, " +
            "  escalation_level = 1, " +
            "  updated_at = CURRENT_TIMESTAMP " +
            "WHERE pull_request_id = ? AND developer_id = ? AND escalation_level < 1 AND assignment_status = 'ASSIGNED'",
            now,
            assignment.pullRequestId(),
            assignment.developerId()
        );

        if (updated == 0) {
            logger.info("sla.level1.skipped pullRequestId={} developerId={} reason=already_processed_or_status_changed",
                assignment.pullRequestId(), assignment.developerId());
            return;
        }

        // 2. Dispatch mock email
        String recipientEmail = assignment.username() + "@company.com";
        String reviewLink = String.format("https://github.com/company/%s/pull/%d", 
            assignment.repoName(), assignment.githubPrNumber());
        
        emailNotificationService.sendReminderEmail(
            recipientEmail,
            assignment.username(),
            assignment.prTitle(),
            assignment.repoName(),
            assignment.complexityScore() != null ? assignment.complexityScore() : 0.0,
            reviewLink,
            assignment.assignmentCreatedAt(),
            hoursWaiting
        );

        // 3. Emit Domain Event
        ReviewReminderSentEvent event = new ReviewReminderSentEvent(
            assignment.pullRequestId(),
            assignment.developerId(),
            assignment.username(),
            now
        );
        eventPublisher.publishEvent(event);
        logger.info("event.emitted type=REVIEW_REMINDER_SENT pullRequestId={} developerId={}", 
            assignment.pullRequestId(), assignment.developerId());
    }

    private void triggerLevel2Stale(AssignmentEscalationMeta assignment, LocalDateTime now, long hoursWaiting) {
        System.out.println("[DEBUG][EscalationEngine] level2.triggered pullRequestId=" + assignment.pullRequestId() 
            + " developerId=" + assignment.developerId() + " username=" + assignment.username());
        logger.info("sla.level2.triggered pullRequestId={} developerId={} username={} hoursWaiting={}", 
            assignment.pullRequestId(), assignment.developerId(), assignment.username(), hoursWaiting);

        // 1. Transactionally update state
        int updated = jdbcTemplate.update(
            "UPDATE reviewer_assignments SET " +
            "  assignment_status = 'STALE', " +
            "  escalated_at = ?, " +
            "  escalation_level = 2, " +
            "  updated_at = CURRENT_TIMESTAMP " +
            "WHERE pull_request_id = ? AND developer_id = ? AND escalation_level < 2 AND assignment_status IN ('ASSIGNED', 'REMINDER_SENT')",
            now,
            assignment.pullRequestId(),
            assignment.developerId()
        );

        if (updated == 0) {
            logger.info("sla.level2.skipped pullRequestId={} developerId={} reason=already_processed_or_status_changed",
                assignment.pullRequestId(), assignment.developerId());
            return;
        }

        // 2. Dispatch mock email to alerting channel
        String alertEmail = assignment.username() + "-manager@company.com";
        emailNotificationService.sendStaleEmail(
            alertEmail,
            assignment.username(),
            assignment.prTitle(),
            assignment.repoName(),
            hoursWaiting
        );

        // 3. Emit Domain Event
        PullRequestStaleEvent event = new PullRequestStaleEvent(
            assignment.pullRequestId(),
            assignment.developerId(),
            assignment.username(),
            now
        );
        eventPublisher.publishEvent(event);
        logger.info("event.emitted type=PULL_REQUEST_STALE pullRequestId={} developerId={}", 
            assignment.pullRequestId(), assignment.developerId());
    }

    private void triggerLevel3Reassignment(AssignmentEscalationMeta assignment, LocalDateTime now) {
        System.out.println("[DEBUG][EscalationEngine] level3.triggered pullRequestId=" + assignment.pullRequestId() 
            + " developerId=" + assignment.developerId() + " username=" + assignment.username());
        logger.info("sla.level3.triggered pullRequestId={} developerId={} username={}", 
            assignment.pullRequestId(), assignment.developerId(), assignment.username());

        // A. Capture other active reviewer IDs before modifying the status
        List<Long> otherActiveReviewerIds = jdbcTemplate.query(
            "SELECT developer_id FROM reviewer_assignments WHERE pull_request_id = ? AND assignment_status IN ('ASSIGNED', 'REMINDER_SENT', 'STALE') AND developer_id != ?",
            (rs, rowNum) -> rs.getLong("developer_id"),
            assignment.pullRequestId(),
            assignment.developerId()
        );

        // B. Transactionally transition stalled assignment to REASSIGNED audit state
        int updated = jdbcTemplate.update(
            "UPDATE reviewer_assignments SET " +
            "  assignment_status = 'REASSIGNED', " +
            "  reassigned_at = ?, " +
            "  escalation_level = 3, " +
            "  updated_at = CURRENT_TIMESTAMP " +
            "WHERE pull_request_id = ? AND developer_id = ? AND escalation_level < 3 AND assignment_status IN ('ASSIGNED', 'REMINDER_SENT', 'STALE')",
            now,
            assignment.pullRequestId(),
            assignment.developerId()
        );

        if (updated == 0) {
            logger.info("sla.level3.skipped pullRequestId={} developerId={} reason=already_processed_or_status_changed",
                assignment.pullRequestId(), assignment.developerId());
            return;
        }

        // C. Rebuild ExpertiseCalculatedEvent from scratch for this PR context
        List<String> filePaths = jdbcTemplate.query(
            "SELECT file_path FROM pull_request_files WHERE pull_request_id = ?",
            (rs, rowNum) -> rs.getString("file_path"),
            assignment.pullRequestId()
        );

        List<Long> expertiseCandidates = new ArrayList<>();
        List<Double> expertiseScores = new ArrayList<>();

        if (!filePaths.isEmpty()) {
            String inClause = filePaths.stream().map(f -> "?").collect(Collectors.joining(","));
            String sqlCandidates = String.format(
                "SELECT developer_id, SUM(expertise_score) AS cumulative_score " +
                "FROM developer_file_expertise " +
                "WHERE repository_id = ? AND file_path IN (%s) " +
                "GROUP BY developer_id " +
                "ORDER BY cumulative_score DESC",
                inClause
            );

            List<Object> queryArgs = new ArrayList<>();
            queryArgs.add(assignment.repositoryId());
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

        ExpertiseCalculatedEvent expertiseCalculatedEvent = new ExpertiseCalculatedEvent(
            assignment.pullRequestId(),
            assignment.repositoryId(),
            expertiseCandidates,
            expertiseScores
        );

        // D. Rerun the overloaded AssignmentService routing with exclusions to fetch the next-best reviewer
        // We exclude the author (handled internally by AssignmentService) and the stalled reviewer ID explicitly.
        assignmentService.assignReviewers(expertiseCalculatedEvent, List.of(assignment.developerId()));

        // E. Find newly assigned reviewer (active status 'ASSIGNED' and NOT part of previously other active reviewers)
        List<NewReviewerMeta> currentActiveReviewers = jdbcTemplate.query(
            "SELECT ra.developer_id, d.username FROM reviewer_assignments ra " +
            "JOIN developers d ON ra.developer_id = d.id " +
            "WHERE ra.pull_request_id = ? AND ra.assignment_status = 'ASSIGNED'",
            (rs, rowNum) -> new NewReviewerMeta(rs.getLong("developer_id"), rs.getString("username")),
            assignment.pullRequestId()
        );

        NewReviewerMeta newReviewer = null;
        for (NewReviewerMeta candidate : currentActiveReviewers) {
            if (!otherActiveReviewerIds.contains(candidate.developerId())) {
                newReviewer = candidate;
                break;
            }
        }

        if (newReviewer == null) {
            logger.warn("sla.level3.reassignment.failed pullRequestId={} previousReviewer={} reason=no_next_best_reviewer_found",
                assignment.pullRequestId(), assignment.username());
            return;
        }

        System.out.println("[DEBUG][EscalationEngine] level3.reassigned pullRequestId=" + assignment.pullRequestId() 
            + " previousReviewer=" + assignment.username() + " newReviewer=" + newReviewer.username());
        logger.info("sla.level3.reassigned pullRequestId={} previousReviewer={} newReviewer={}", 
            assignment.pullRequestId(), assignment.username(), newReviewer.username());

        // F. Dispatch notifications to both developers
        String previousReviewerEmail = assignment.username() + "@company.com";
        String newReviewerEmail = newReviewer.username() + "@company.com";
        emailNotificationService.sendReassignmentEmail(
            previousReviewerEmail,
            assignment.username(),
            newReviewerEmail,
            newReviewer.username(),
            assignment.prTitle(),
            assignment.repoName()
        );

        // G. Emit domain event
        ReviewerReassignedEvent event = new ReviewerReassignedEvent(
            assignment.pullRequestId(),
            assignment.repositoryId(),
            assignment.developerId(),
            newReviewer.developerId(),
            now
        );
        eventPublisher.publishEvent(event);
        logger.info("event.emitted type=REVIEWER_REASSIGNED pullRequestId={} previousReviewerId={} newReviewerId={}", 
            assignment.pullRequestId(), assignment.developerId(), newReviewer.developerId());
    }

    public record AssignmentEscalationMeta(
        Long pullRequestId,
        Long developerId,
        LocalDateTime assignmentCreatedAt,
        String assignmentStatus,
        int escalationLevel,
        String username,
        String prTitle,
        Long githubPrNumber,
        Double complexityScore,
        String repoName,
        Long repositoryId
    ) {}

    private record NewReviewerMeta(
        Long developerId,
        String username
    ) {}
}
