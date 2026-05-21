package prflow.spring_backend.engines.complexity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for deterministic complexity scoring behavior.
 */
class ComplexityCalculatorTest {

    private final ComplexityCalculator calculator = new ComplexityCalculator(new ComplexityConfig());

    @Test
    void smallPrShouldResolveToLowWithDefaultWeights() {
        ComplexityBreakdown breakdown = calculator.calculate(2, 20, 5, 1);

        assertEquals(ComplexityLevel.LOW, breakdown.complexityLevel());
        assertTrue(breakdown.finalScore() >= 0.0);
        assertTrue(breakdown.finalScore() <= 20.0);
    }

    @Test
    void largeCrossModulePrShouldResolveToCritical() {
        ComplexityBreakdown breakdown = calculator.calculate(25, 800, 250, 7);

        assertEquals(ComplexityLevel.CRITICAL, breakdown.complexityLevel());
        assertTrue(breakdown.finalScore() > 70.0);
        assertEquals(100.0, breakdown.directorySpreadScore());
    }

    @Test
    void additionsShouldInfluenceDiffMoreThanDeletions() {
        // Keeps V1 intent explicit: additions are weighted above deletions.
        ComplexityBreakdown heavyAdditions = calculator.calculate(8, 400, 20, 3);
        ComplexityBreakdown heavyDeletions = calculator.calculate(8, 20, 400, 3);

        assertTrue(heavyAdditions.diffScore() > heavyDeletions.diffScore());
    }
}
