package com.btg.infrastructure.web.task.dto.response;

public record TaskMemberResponse(
    Long id,
    UserResponse user,
    Double completionRate,
    Integer completedDays,
    Integer totalDays,
    Long joinedAt
) {}
