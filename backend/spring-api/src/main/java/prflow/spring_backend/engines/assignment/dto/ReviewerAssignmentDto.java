package prflow.spring_backend.engines.assignment.dto;

import java.time.LocalDateTime;

/**
 * Data transfer object representing a persisted reviewer assignment.
 */
public record ReviewerAssignmentDto(
    Long id,
    Long pullRequestId,
    Long developerId,
    String developerUsername,
    double assignmentScore,
    String assignmentReason,
    String assignmentType,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
