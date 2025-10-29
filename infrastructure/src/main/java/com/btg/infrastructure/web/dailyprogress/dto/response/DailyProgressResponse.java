package com.btg.infrastructure.web.dailyprogress.dto.response;

public record DailyProgressResponse(
    String date,
    Boolean completed,
    String completedAt
) {}
