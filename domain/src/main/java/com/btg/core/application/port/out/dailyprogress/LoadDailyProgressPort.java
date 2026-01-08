package com.btg.core.application.port.out.dailyprogress;

import java.util.List;

public interface LoadDailyProgressPort {

    List<DailyProgress> loadByTaskMemberId(Long taskMemberId);

    int countCompletedByTaskMemberId(Long taskMemberId);

    int countCompletedByTaskId(Long taskId);

    int countTotalByTaskId(Long taskId);

    record DailyProgress(
        Long id,
        Long taskMemberId,
        String date,
        Boolean completed,
        String completedAt
    ) {}
}
