package prflow.spring_backend.engines.escalation;

import java.time.LocalDateTime;

/**
 * Event contract emitted when a stalled reviewer is reassigned to the next-best developer (Level 3).
 */
public record ReviewerReassignedEvent(
    Long pullRequestId,
    Long repositoryId,
    Long previousReviewerId,
    Long newReviewerId,
    LocalDateTime reassignedAt
) {}
