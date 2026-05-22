package prflow.spring_backend.engines.assignment.model;

import prflow.spring_backend.enums.DeveloperSeniority;

/**
 * Data model for keeping intermediate scoring and selection states of a candidate reviewer.
 */
public record ReviewerCandidate(
    Long developerId,
    String username,
    DeveloperSeniority seniority,
    double expertiseScore,
    int activeReviewsCount,
    double finalScore,
    String reason,
    String assignmentType
) {}
