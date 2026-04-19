package com.btg.core.application.port.out.task;

public interface SaveTaskPort {
    Task save(
        Long groupId,
        Long createdByUserId,
        String title,
        String description,
        String status,
        String startDate,
        String endDate,
        Integer totalDays,
        Integer maxParticipants
    );

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