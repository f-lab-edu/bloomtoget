package com.btg.infrastructure.web.dailyprogress.dto.response;

import java.util.List;

public record MyDailyProgressResponse(
    Long taskId,
    Long userId,
    List<DailyProgressResponse> dailyRecords
) {}
