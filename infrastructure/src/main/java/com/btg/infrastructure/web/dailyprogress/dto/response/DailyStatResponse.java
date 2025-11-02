package com.btg.infrastructure.web.dailyprogress.dto.response;

public record DailyStatResponse(
    String date,
    Integer completedCount,
    Integer totalParticipants,
    Double completionRate
) {}
