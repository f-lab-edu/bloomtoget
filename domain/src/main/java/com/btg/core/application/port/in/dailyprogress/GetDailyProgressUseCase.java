package com.btg.core.application.port.in.dailyprogress;

import java.util.List;

public interface GetDailyProgressUseCase {
    DailyProgressSummaryResult getDailyProgressSummary(Long taskId);
    MyDailyProgressResult getMyDailyProgress(Long taskId, Long userId);

    record DailyProgressSummaryResult(
        Long taskId,
        String startDate,
        String endDate,
        List<DailyStat> dailyStats
    ) {}

    record DailyStat(
        String date,
        Integer completedCount,
        Integer totalParticipants,
        Double completionRate
    ) {}

    record MyDailyProgressResult(
        Long taskId,
        Long userId,
        List<DailyRecord> dailyRecords
    ) {}

    record DailyRecord(
        String date,
        Boolean completed,
        String completedAt
    ) {}
}
