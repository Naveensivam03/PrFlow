package prflow.spring_backend.engines.assignment.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration options for the V1 Assignment decision engine.
 */
@Configuration
@ConfigurationProperties(prefix = "prflow.engines.assignment")
public class AssignmentConfig {

    private int defaultReviewerLimit = 2;
    private double juniorGrowthRatio = 0.20;
    private double complexityThreshold = 7.0;
    private double queuePenaltyWeight = 2.0;

    public int getDefaultReviewerLimit() {
        return defaultReviewerLimit;
    }

    public void setDefaultReviewerLimit(int defaultReviewerLimit) {
        this.defaultReviewerLimit = defaultReviewerLimit;
    }

    public double getJuniorGrowthRatio() {
        return juniorGrowthRatio;
    }

    public void setJuniorGrowthRatio(double juniorGrowthRatio) {
        this.juniorGrowthRatio = juniorGrowthRatio;
    }

    public double getComplexityThreshold() {
        return complexityThreshold;
    }

    public void setComplexityThreshold(double complexityThreshold) {
        this.complexityThreshold = complexityThreshold;
    }

    public double getQueuePenaltyWeight() {
        return queuePenaltyWeight;
    }

    public void setQueuePenaltyWeight(double queuePenaltyWeight) {
        this.queuePenaltyWeight = queuePenaltyWeight;
    }
}
