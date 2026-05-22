package prflow.spring_backend.modules.pullrequest.event;

/**
 * Event published internally when a PULL_REQUEST_MERGED webhook event is received.
 */
public record PullRequestMergedEvent(
    Long pullRequestId,
    Long repositoryId,
    String deliveryId
) {}
