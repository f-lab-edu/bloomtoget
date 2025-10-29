package com.btg.infrastructure.web.task.dto.response;

public record TaskResponse(
    Long id,
    Long groupId,
    String title,
    String description,
    String status,  // "RECRUITING", "IN_PROGRESS", "COMPLETED", "CANCELLED"
    String startDate,
    String endDate,
    Integer totalDays,
    Integer participantCount,
    Integer maxParticipants,
    Double overallCompletionRate,
    UserResponse createdBy,
    String createdAt,
    String updatedAt
) {}
