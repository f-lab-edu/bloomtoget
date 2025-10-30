package com.btg.core.application.port.in.task;

public interface GetTaskUseCase {
    TaskDetailResult getTask(Long taskId, Long userId);

    record TaskDetailResult(
        Long id,
        Long groupId,
        String title,
        String description,
        String status,  // "RECRUITING", "IN_PROGRESS", "COMPLETED", "CANCELLED"
        String startDate,
        String endDate,
        Integer totalDays,
        Integer participantCount,
        Integer maxParticipants,
        Double overallCompletionRate,
        UserInfo createdBy,
        String createdAt,
        String updatedAt,
        Boolean isParticipating,
        Double myCompletionRate
    ) {}

    record UserInfo(
        Long id,
        String email,
        String name
    ) {}
}
