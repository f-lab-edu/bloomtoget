package com.btg.core.application.port.out.task;

import java.util.List;
import java.util.Optional;

public interface LoadTaskPort {

    Optional<Task> loadById(Long taskId);

    PagedTask loadByGroupId(Long groupId, String status, int page, int size);

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

    record PagedTask(
        List<Task> content,
        int totalElements,
        int totalPages,
        int page,
        int size
    ) {}
}
