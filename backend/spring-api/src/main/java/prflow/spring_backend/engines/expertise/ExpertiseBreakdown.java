package prflow.spring_backend.engines.expertise;

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
