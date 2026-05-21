package prflow.spring_backend.engines.complexity;

/**
 * Explainable scoring snapshot for a single complexity calculation run.
 *
 * <p>All contributing and placeholder signals are retained to support
 * deterministic audits, replay diagnostics, and future weight tuning.
 */
public record ComplexityBreakdown(
    double fileCountScore,
    double additionsScore,
    double deletionsScore,
    double diffScore,
    double directorySpreadScore,
    double cycloPlaceholderScore,
    double coveragePlaceholderScore,
    double finalScore,
    ComplexityLevel complexityLevel
) {}
