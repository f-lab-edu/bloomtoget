package com.btg.core.application.port.in.task;

import java.util.List;

public interface ListTasksUseCase {
    PagedTaskResult listTasks(ListTasksQuery query);

    record ListTasksQuery(
        Long userId,
        Long groupId,  // Optional: filter by group
        String status,  // "ALL", "RECRUITING", "IN_PROGRESS", "COMPLETED", "CANCELLED"
        Integer page,
        Integer size
    ) {
        public ListTasksQuery {
            // Self-validation
            if (userId == null || userId <= 0) {
                throw new IllegalArgumentException("User ID is required");
            }
            // Optional: groupId
            if (groupId != null && groupId <= 0) {
                throw new IllegalArgumentException("Group ID must be positive");
            }
            // Optional: status (default "ALL")
            if (status != null && !status.isBlank()) {
                if (!status.equals("ALL") && !status.equals("RECRUITING") &&
                    !status.equals("IN_PROGRESS") && !status.equals("COMPLETED") &&
                    !status.equals("CANCELLED")) {
                    throw new IllegalArgumentException(
                        "Status must be one of: ALL, RECRUITING, IN_PROGRESS, COMPLETED, CANCELLED"
                    );
                }
            }
            // Optional: page and size
            if (page != null && page < 0) {
                throw new IllegalArgumentException("Page must not be negative");
            }
            if (size != null && (size < 1 || size > 100)) {
                throw new IllegalArgumentException("Size must be between 1 and 100");
            }
        }
    }

    record PagedTaskResult(
        List<TaskSummary> content,
        Integer totalElements,
        Integer totalPages,
        Integer page,
        Integer size
    ) {}

    record TaskSummary(
        Long id,
        Long groupId,
        String title,
        String description,
        String status,
        String startDate,
        String endDate,
        Integer totalDays,
        Integer participantCount,
        Integer maxParticipants,
        Double overallCompletionRate,
        UserInfo createdBy,
        String createdAt,
        String updatedAt
    ) {}

    record UserInfo(
        Long id,
        String email,
        String name
    ) {}
}
