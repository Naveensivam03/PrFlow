package prflow.spring_backend.modules.pullrequest.event;

/**
 * Event published internally when a REVIEW_SUBMITTED webhook event is received.
 */
public record ReviewSubmittedEvent(
    Long pullRequestId,
    Long repositoryId,
    String deliveryId
) {}
