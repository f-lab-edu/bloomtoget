package com.btg.infrastructure.web.dailyprogress.dto.response;

import java.util.List;

public record DailyProgressSummaryResponse(
    Long taskId,
    String startDate,
    String endDate,
    List<DailyStatResponse> dailyStats
) {}
