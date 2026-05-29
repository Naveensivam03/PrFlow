package prflow.spring_backend.engines.complexity.model;

/**
 * Canonical complexity tiers consumed by orchestration policies.
 *
 * <p>
 * Numeric scores remain tunable and auditable, while this enum provides a
 * stable
 * readability layer for workflow decisions and downstream event consumers.
 */
public enum ComplexityLevel {
  LOW,
  MEDIUM,
  HIGH,
  CRITICAL;

  /**
   * Maps the bounded final score (0..100) into a deterministic complexity tier.
   */
  public static ComplexityLevel fromScore(double finalScore) {
    if (finalScore <= 20.0) {
      return LOW;
    }
    if (finalScore <= 45.0 && finalScore > 20) {
      return MEDIUM;
    }
    if (finalScore <= 70.0 && finalScore > 45) {
      return HIGH;
    }
    return CRITICAL;
  }
}
