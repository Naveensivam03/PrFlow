package prflow.spring_backend.engines.complexity.event;

import prflow.spring_backend.engines.complexity.model.ComplexityLevel;

/**
 * Downstream enrichment handoff emitted after complexity persistence succeeds.
 */
// record is immutable data storage boilerplate code .
public record ComplexityCalculatedEvent(
    Long pullRequestId,
    Long repositoryId,
    double complexityScore,
    ComplexityLevel complexityLevel
) {}
