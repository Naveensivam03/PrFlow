package prflow.spring_backend.engines.assignment;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for isolated deterministic Assignment Engine V1 scoring rules.
 */
class AssignmentScoringServiceTest {

    private final AssignmentConfig config = new AssignmentConfig();
    private final AssignmentScoringService scoringService = new AssignmentScoringService(config);

    @Test
    void zeroActiveReviewsShouldApplyNoPenalty() {
        double score = scoringService.calculateScore(10.0, 0);
        assertEquals(10.0, score);
    }

    @Test
    void activeReviewsShouldGracefullyDecayFinalScore() {
        // Expertise: 10.0, Active: 1. Penalty = 1 * 2 = 2. Multiplier = 1.0 / (1.0 + 2.0) = 0.3333333333333333
        double scoreOneReview = scoringService.calculateScore(10.0, 1);
        assertEquals(10.0 / 3.0, scoreOneReview, 1e-9);

        // Expertise: 10.0, Active: 2. Penalty = 2 * 2 = 4. Multiplier = 1.0 / (1.0 + 4.0) = 0.20
        double scoreTwoReviews = scoringService.calculateScore(10.0, 2);
        assertEquals(2.0, scoreTwoReviews, 1e-9);

        assertTrue(scoreOneReview > scoreTwoReviews, "More active reviews must result in a lower final score.");
    }

    @Test
    void buildReasonShouldContainDetailedScoringMath() {
        String reason = scoringService.buildReason(15.5, 2, 3.1);
        
        assertTrue(reason.contains("Expertise: 15.50"));
        assertTrue(reason.contains("Active Reviews: 2"));
        assertTrue(reason.contains("Penalty: 4.0"));
        assertTrue(reason.contains("Multiplier: 0.200"));
        assertTrue(reason.contains("Final Score: 3.10"));
    }
}
