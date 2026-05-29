package prflow.spring_backend.engines.complexity.event;

public record PullRequestAnalyzedEvent(
    Long pullRequestId,
    Long repositoryId,
    String deliveryId
) {}
