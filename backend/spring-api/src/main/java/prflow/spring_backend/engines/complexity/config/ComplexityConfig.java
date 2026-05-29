package prflow.spring_backend.engines.complexity.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Engine-local configuration for deterministic complexity normalization and weighting.
 *
 * <p>V1 uses only diff and directory final weights. Future weights for cyclomatic and
 * coverage dimensions are intentionally retained as placeholders to preserve a stable
 * formula evolution path.
 */
@Configuration
@ConfigurationProperties(prefix = "prflow.engines.complexity")
public class ComplexityConfig {

    private Weights weights = new Weights();
    private Normalization normalization = new Normalization();

    public Weights getWeights() {
        return weights;
    }

    public void setWeights(Weights weights) {
        this.weights = weights;
    }

    public Normalization getNormalization() {
        return normalization;
    }

    public void setNormalization(Normalization normalization) {
        this.normalization = normalization;
    }

    public static class Weights {
        // Diff composition weights (V1 active).
        private double fileCountInDiff = 0.30;
        private double additionsInDiff = 0.50;
        private double deletionsInDiff = 0.20;

        // Final formula weights (V1 active).
        private double diffV1 = 0.55;
        private double directoryV1 = 0.45;

        // Long-term architecture weights (V1 inactive placeholders).
        private double diffFuture = 0.30;
        private double directoryFuture = 0.25;
        private double cycloFuture = 0.25;
        private double coverageFuture = 0.20;

        public double getFileCountInDiff() {
            return fileCountInDiff;
        }

        public void setFileCountInDiff(double fileCountInDiff) {
            this.fileCountInDiff = fileCountInDiff;
        }

        public double getAdditionsInDiff() {
            return additionsInDiff;
        }

        public void setAdditionsInDiff(double additionsInDiff) {
            this.additionsInDiff = additionsInDiff;
        }

        public double getDeletionsInDiff() {
            return deletionsInDiff;
        }

        public void setDeletionsInDiff(double deletionsInDiff) {
            this.deletionsInDiff = deletionsInDiff;
        }

        public double getDiffV1() {
            return diffV1;
        }

        public void setDiffV1(double diffV1) {
            this.diffV1 = diffV1;
        }

        public double getDirectoryV1() {
            return directoryV1;
        }

        public void setDirectoryV1(double directoryV1) {
            this.directoryV1 = directoryV1;
        }

        public double getDiffFuture() {
            return diffFuture;
        }

        public void setDiffFuture(double diffFuture) {
            this.diffFuture = diffFuture;
        }

        public double getDirectoryFuture() {
            return directoryFuture;
        }

        public void setDirectoryFuture(double directoryFuture) {
            this.directoryFuture = directoryFuture;
        }

        public double getCycloFuture() {
            return cycloFuture;
        }

        public void setCycloFuture(double cycloFuture) {
            this.cycloFuture = cycloFuture;
        }

        public double getCoverageFuture() {
            return coverageFuture;
        }

        public void setCoverageFuture(double coverageFuture) {
            this.coverageFuture = coverageFuture;
        }
    }

    public static class Normalization {
        // Tiered thresholds for bounded file-count normalization.
        private int filesLowMax = 5;
        private int filesMediumMax = 15;

        // Tiered thresholds for bounded directory-spread normalization.
        private int directoriesLowMax = 1;
        private int directoriesMediumMax = 4;

        // High-watermarks for bounded linear normalization.
        private int additionsHighWatermark = 500;
        private int deletionsHighWatermark = 700;

        public int getFilesLowMax() {
            return filesLowMax;
        }

        public void setFilesLowMax(int filesLowMax) {
            this.filesLowMax = filesLowMax;
        }

        public int getFilesMediumMax() {
            return filesMediumMax;
        }

        public void setFilesMediumMax(int filesMediumMax) {
            this.filesMediumMax = filesMediumMax;
        }

        public int getDirectoriesLowMax() {
            return directoriesLowMax;
        }

        public void setDirectoriesLowMax(int directoriesLowMax) {
            this.directoriesLowMax = directoriesLowMax;
        }

        public int getDirectoriesMediumMax() {
            return directoriesMediumMax;
        }

        public void setDirectoriesMediumMax(int directoriesMediumMax) {
            this.directoriesMediumMax = directoriesMediumMax;
        }

        public int getAdditionsHighWatermark() {
            return additionsHighWatermark;
        }

        public void setAdditionsHighWatermark(int additionsHighWatermark) {
            this.additionsHighWatermark = additionsHighWatermark;
        }

        public int getDeletionsHighWatermark() {
            return deletionsHighWatermark;
        }

        public void setDeletionsHighWatermark(int deletionsHighWatermark) {
            this.deletionsHighWatermark = deletionsHighWatermark;
        }
    }
}
