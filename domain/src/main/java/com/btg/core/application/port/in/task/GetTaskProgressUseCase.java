package com.btg.core.application.port.in.task;

public interface GetTaskProgressUseCase {

    TaskProgressResult getTaskProgress(Long taskId);

    record TaskProgressResult(
        Long taskId,
        Integer totalParticipants,
        Integer totalDays,
        Double overallCompletionRate,
        Double averageCompletionRate
    ) {}
}
