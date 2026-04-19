package com.btg.core.application.port.out.task;

public interface UpdateTaskPort {

    Task updateStatus(Long taskId, String newStatus);

    Task update(Long taskId, String title, String description, Integer maxParticipants);

    record Task(
        Long id,
        Long groupId,
        Long createdByUserId,
        String title,
        String description,
        String status,
        String startDate,
        String endDate,
        Integer totalDays,
        Integer maxParticipants,
        Long createdAt,
        Long updatedAt
    ) {}
}
