package prflow.spring_backend.engines.escalation;

import java.time.LocalDateTime;

/**
 * Event contract emitted when a Pull Request review workflow transitions to a STALE bottleneck state (Level 2).
 */
public record PullRequestStaleEvent(
    Long pullRequestId,
    Long developerId,
    String reviewerUsername,
    LocalDateTime escalatedAt
) {}
