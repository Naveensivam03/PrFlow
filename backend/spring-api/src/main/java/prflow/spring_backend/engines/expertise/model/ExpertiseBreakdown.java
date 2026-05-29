package prflow.spring_backend.engines.expertise.model;

/**
 * Snapshot breakdown representing calculated contribution details for auditable explainability.
 */
public record ExpertiseBreakdown(
    double fileTouchScore,
    double scopeTouchScore,
    double recencyWeight,
    double normalizedScore,
    double finalExpertiseScore
) {}
