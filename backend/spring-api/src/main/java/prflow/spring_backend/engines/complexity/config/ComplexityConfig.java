package prflow.spring_backend.engines.complexity.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Engine-local configuration for deterministic complexity normalization and weighting.
 *
 * <p>V1 uses only diff and directory final weights. Future weights for cyclomatic and
 * coverage dimensions are intentionally retained as placeholders to preserve a stable
 * formula evolution path.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "prflow.engines.complexity")
public class ComplexityConfig {

    private Weights weights = new Weights();
    private Normalization normalization = new Normalization();

    /**
     * Weights derived from PRFlow Complexity Engine V1 implementation guide.
     * Weights prioritize additions over deletions and directory spread over file count
     * to reflect higher review overhead and operational risk.
     */
    @Data
    public static class Weights {

        // Diff composition weights (V1 active).
        // Additions weighted higher (0.50) than deletions (0.20) as they introduce new logic.
        private double fileCountInDiff = 0.30;
        private double additionsInDiff = 0.50;
        private double deletionsInDiff = 0.20;

        // Final formula weights (V1 active).
        // Directory spread (0.45) is weighted strongly due to cross-module operational risk.
        private double diffV1 = 0.55;
        private double directoryV1 = 0.45;

        // Long-term architecture weights (V1 inactive placeholders).
        // Planned evolution to include cyclomatic complexity and test coverage.
        private double diffFuture = 0.30;
        private double directoryFuture = 0.25;
        private double cycloFuture = 0.25;
        private double coverageFuture = 0.20;
    }

    /**
     * Bounded normalization thresholds to ensure deterministic scoring.
     * Prevents outliers (e.g., giant PRs) from exploding the score scale,
     * maintaining stable orchestration policies.
     */
    @Data
    public static class Normalization {

        // Tiered thresholds for bounded file-count normalization.
        // 0..5 -> Low, 6..15 -> Medium, 16+ -> High/Critical.
        private int filesLowMax = 5;
        private int filesMediumMax = 15;

        // Tiered thresholds for bounded directory-spread normalization.
        // 1 -> Low, 2..4 -> Medium, 5+ -> High/Critical.
        private int directoriesLowMax = 1;
        private int directoriesMediumMax = 4;

        // High-watermarks for bounded linear normalization.
        // Counts above these watermarks are capped at 100% impact.
        private int additionsHighWatermark = 500;
        private int deletionsHighWatermark = 700;
    }
}
