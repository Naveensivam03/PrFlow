package prflow.spring_backend.engines.escalation.event;

import java.time.LocalDateTime;

/**
 * Event contract emitted when a Level 1 review reminder is sent to an assigned reviewer.
 */
public record ReviewReminderSentEvent(
    Long pullRequestId,
    Long developerId,
    String reviewerUsername,
    LocalDateTime sentAt
) {}
