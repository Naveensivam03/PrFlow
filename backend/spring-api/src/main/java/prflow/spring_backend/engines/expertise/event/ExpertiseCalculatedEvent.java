package prflow.spring_backend.engines.expertise.event;

import java.util.List;

/**
 * Event contract emitted after completing transactional file-level expertise updates.
 * Consumed by downstream reviewer assignment engines.
 */
public record ExpertiseCalculatedEvent(
    Long pullRequestId,
    Long repositoryId,
    List<Long> expertiseCandidates,
    List<Double> expertiseScores
) {}
