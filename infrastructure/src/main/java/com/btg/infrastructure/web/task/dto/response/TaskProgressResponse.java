package com.btg.infrastructure.web.task.dto.response;

public record TaskProgressResponse(
    Long taskId,
    Integer totalParticipants,
    Integer totalDays,
    Double overallCompletionRate,
    Double averageCompletionRate
) {}
