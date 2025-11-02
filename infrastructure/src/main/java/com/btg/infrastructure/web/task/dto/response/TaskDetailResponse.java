package com.btg.infrastructure.web.task.dto.response;

public record TaskDetailResponse(
    Long id,
    Long groupId,
    String title,
    String description,
    String status,
    String startDate,
    String endDate,
    Integer totalDays,
    Integer participantCount,
    Integer maxParticipants,
    Double overallCompletionRate,
    UserResponse createdBy,
    String createdAt,
    String updatedAt,
    Boolean isParticipating,
    Double myCompletionRate
) {}
