package prflow.spring_backend.engines.complexity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Component;

/**
 * Deterministic complexity scorer.
 *
 * <p>Responsibilities:
 * - normalize raw PR change signals into bounded scores
 * - compute V1 weighted diff and final scores
 * - retain placeholder dimensions for future formula expansion
 */
@Component
public class ComplexityCalculator {

    private final ComplexityConfig config;

    public ComplexityCalculator(ComplexityConfig config) {
        this.config = config;
    }

    public ComplexityBreakdown calculate(int totalFilesChanged, int totalAdditions, int totalDeletions, int uniqueDirectoriesTouched) {
        double fileCountScore = normalizeFileCount(totalFilesChanged);
        double additionsScore = normalizeBounded(totalAdditions, config.getNormalization().getAdditionsHighWatermark());
        double deletionsScore = normalizeBounded(totalDeletions, config.getNormalization().getDeletionsHighWatermark());
        double directorySpreadScore = normalizeDirectorySpread(uniqueDirectoriesTouched);

        double diffScore =
            (fileCountScore * config.getWeights().getFileCountInDiff()) +
            (additionsScore * config.getWeights().getAdditionsInDiff()) +
            (deletionsScore * config.getWeights().getDeletionsInDiff());

        // V1 placeholders: retained in breakdown to keep future expansion explicit.
        double cycloPlaceholderScore = 0.0;
        double coveragePlaceholderScore = 0.0;

        double finalScore =
            (diffScore * config.getWeights().getDiffV1()) +
            (directorySpreadScore * config.getWeights().getDirectoryV1());

        finalScore = round2(finalScore);
        ComplexityLevel complexityLevel = ComplexityLevel.fromScore(finalScore);

        return new ComplexityBreakdown(
            round2(fileCountScore),
            round2(additionsScore),
            round2(deletionsScore),
            round2(diffScore),
            round2(directorySpreadScore),
            cycloPlaceholderScore,
            coveragePlaceholderScore,
            finalScore,
            complexityLevel
        );
    }

    /**
     * Maps file count into low/medium/high normalized tiers.
     */
    private double normalizeFileCount(int filesChanged) {
        if (filesChanged <= config.getNormalization().getFilesLowMax()) {
            return 20.0;
        }
        if (filesChanged <= config.getNormalization().getFilesMediumMax()) {
            return 60.0;
        }
        return 100.0;
    }

    /**
     * Maps unique directory spread into low/medium/high risk tiers.
     */
    private double normalizeDirectorySpread(int uniqueDirectoriesTouched) {
        if (uniqueDirectoriesTouched <= config.getNormalization().getDirectoriesLowMax()) {
            return 20.0;
        }
        if (uniqueDirectoriesTouched <= config.getNormalization().getDirectoriesMediumMax()) {
            return 60.0;
        }
        return 100.0;
    }

    /**
     * Linear bounded normalization capped to 100 to prevent outlier explosions.
     */
    private double normalizeBounded(int rawValue, int highWatermark) {
        if (rawValue <= 0) {
            return 0.0;
        }
        if (highWatermark <= 0) {
            return 100.0;
        }

        double bounded = (rawValue * 100.0) / highWatermark;
        return Math.min(bounded, 100.0);
    }

    /**
     * Normalizes decimal precision for persistence and log consistency.
     */
    private double round2(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
