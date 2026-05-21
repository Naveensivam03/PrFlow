package prflow.spring_backend.engines.expertise;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Engine-local configuration for deterministic expertise scoring and decay thresholds.
 */
@Configuration
@ConfigurationProperties(prefix = "prflow.engines.expertise")
public class ExpertiseConfig {

    private Weights weights = new Weights();
    private Normalization normalization = new Normalization();
    private Decay decay = new Decay();

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

    public Decay getDecay() {
        return decay;
    }

    public void setDecay(Decay decay) {
        this.decay = decay;
    }

    public static class Weights {
        private double fileTouch = 0.70;
        private double scopeTouch = 0.30;
        
        // Future placeholders
        private double commitWeightFuture = 0.60;
        private double reviewWeightFuture = 0.40;

        public double getFileTouch() {
            return fileTouch;
        }

        public void setFileTouch(double fileTouch) {
            this.fileTouch = fileTouch;
        }

        public double getScopeTouch() {
            return scopeTouch;
        }

        public void setScopeTouch(double scopeTouch) {
            this.scopeTouch = scopeTouch;
        }

        public double getCommitWeightFuture() {
            return commitWeightFuture;
        }

        public void setCommitWeightFuture(double commitWeightFuture) {
            this.commitWeightFuture = commitWeightFuture;
        }

        public double getReviewWeightFuture() {
            return reviewWeightFuture;
        }

        public void setReviewWeightFuture(double reviewWeightFuture) {
            this.reviewWeightFuture = reviewWeightFuture;
        }
    }

    public static class Normalization {
        private double fileTouchHighWatermark = 10.0;
        private double scopeTouchHighWatermark = 25.0;

        public double getFileTouchHighWatermark() {
            return fileTouchHighWatermark;
        }

        public void setFileTouchHighWatermark(double fileTouchHighWatermark) {
            this.fileTouchHighWatermark = fileTouchHighWatermark;
        }

        public double getScopeTouchHighWatermark() {
            return scopeTouchHighWatermark;
        }

        public void setScopeTouchHighWatermark(double scopeTouchHighWatermark) {
            this.scopeTouchHighWatermark = scopeTouchHighWatermark;
        }
    }

    public static class Decay {
        private double under30Days = 1.0;
        private double under90Days = 0.6;
        private double under180Days = 0.4;
        private double olderThan180Days = 0.2;

        public double getUnder30Days() {
            return under30Days;
        }

        public void setUnder30Days(double under30Days) {
            this.under30Days = under30Days;
        }

        public double getUnder90Days() {
            return under90Days;
        }

        public void setUnder90Days(double under90Days) {
            this.under90Days = under90Days;
        }

        public double getUnder180Days() {
            return under180Days;
        }

        public void setUnder180Days(double under180Days) {
            this.under180Days = under180Days;
        }

        public double getOlderThan180Days() {
            return olderThan180Days;
        }

        public void setOlderThan180Days(double olderThan180Days) {
            this.olderThan180Days = olderThan180Days;
        }
    }
}
