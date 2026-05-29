package prflow.spring_backend.engines.assignment.service;

import org.springframework.stereotype.Service;

/**
 * Isolated, deterministic scoring calculator for V1 Reviewer Assignment Engine.
 */
@Service
public class AssignmentScoringService {

    private final AssignmentConfig config;

    public AssignmentScoringService(AssignmentConfig config) {
        this.config = config;
    }

    /**
     * Compute reviewer suitability score using deterministic active queue penalty decay.
     * Formula:
     *   queue_penalty = active_review_count * queue_penalty_weight (default: 2.0)
     *   penalty_multiplier = 1.0 / (1.0 + queue_penalty)
     *   final_score = pr_expertise_score * penalty_multiplier
     */
    public double calculateScore(double expertiseScore, int activeReviewsCount) {
        double queuePenalty = activeReviewsCount * config.getQueuePenaltyWeight();
        double penaltyMultiplier = 1.0 / (1.0 + queuePenalty);
        return expertiseScore * penaltyMultiplier;
    }

    /**
     * Build standard structured reason explaining the math behind the selection.
     */
    public String buildReason(double expertiseScore, int activeReviewsCount, double finalScore) {
        double queuePenalty = activeReviewsCount * config.getQueuePenaltyWeight();
        double penaltyMultiplier = 1.0 / (1.0 + queuePenalty);
        return String.format(
            "Expertise: %.2f | Active Reviews: %d (Penalty: %.1f, Multiplier: %.3f) | Final Score: %.2f",
            expertiseScore,
            activeReviewsCount,
            queuePenalty,
            penaltyMultiplier,
            finalScore
        );
    }
}
