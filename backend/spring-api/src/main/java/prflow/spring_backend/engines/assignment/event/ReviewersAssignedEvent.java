package prflow.spring_backend.engines.assignment.event;

import java.util.List;

/**
 * Event contract emitted after successfully calculating and persisting reviewer assignments.
 */
public record ReviewersAssignedEvent(
    Long pullRequestId,
    Long repositoryId,
    List<Long> assignedReviewerIds
) {}
