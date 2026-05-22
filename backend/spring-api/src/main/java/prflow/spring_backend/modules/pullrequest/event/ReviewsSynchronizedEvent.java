package prflow.spring_backend.modules.pullrequest.event;

import java.util.List;

/**
 * Event emitted downstream after pull request reviews have been successfully synchronized.
 */
public record ReviewsSynchronizedEvent(
    Long pullRequestId,
    Long repositoryId,
    List<Long> synchronizedReviewerIds
) {}
