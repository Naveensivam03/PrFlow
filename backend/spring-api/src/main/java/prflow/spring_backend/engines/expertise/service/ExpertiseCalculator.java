package prflow.spring_backend.engines.expertise.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Component;

/**
 * Deterministic expertise calculator.
 *
 * <p>Responsibilities:
 * - normalize raw decayed file-touch and scope-touch signals into bounded 0..100 scores
 * - apply weighted formula to compute final expertise level
 * - round and format values to preserve persistence scale consistency
 */
@Component
public class ExpertiseCalculator {

    private final ExpertiseConfig config;

    public ExpertiseCalculator(ExpertiseConfig config) {
        this.config = config;
    }

    /**
     * Calculates the bounded familiarity score using deterministic weighted heuristic formula.
     */
    public ExpertiseBreakdown calculate(double fileTouchScore, double scopeTouchScore, double averageRecencyWeight) {
        double normalizedFileTouch = normalizeBounded(fileTouchScore, config.getNormalization().getFileTouchHighWatermark());
        double normalizedScopeTouch = normalizeBounded(scopeTouchScore, config.getNormalization().getScopeTouchHighWatermark());

        double finalScore =
            (normalizedFileTouch * config.getWeights().getFileTouch()) +
            (normalizedScopeTouch * config.getWeights().getScopeTouch());

        finalScore = round2(finalScore);

        // Sum/weighted representation of the score
        double rawNormalized = (normalizedFileTouch * config.getWeights().getFileTouch()) +
            (normalizedScopeTouch * config.getWeights().getScopeTouch());

        return new ExpertiseBreakdown(
            round2(fileTouchScore),
            round2(scopeTouchScore),
            round2(averageRecencyWeight),
            round2(rawNormalized),
            finalScore
        );
    }

    /**
     * Bounded linear normalization preventing permanently high scores or outlier explosions.
     */
    private double normalizeBounded(double rawValue, double highWatermark) {
        if (rawValue <= 0.0) {
            return 0.0;
        }
        if (highWatermark <= 0.0) {
            return 100.0;
        }
        double bounded = (rawValue * 100.0) / highWatermark;
        return Math.min(bounded, 100.0);
    }

    private double round2(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return 0.0;
        }
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
