package com.btg.core.application.port.in.task;

import java.util.List;

public interface UpdateTaskStatusUseCase {

    TaskResult updateStatus(UpdateStatusCommand command);

    record UpdateStatusCommand(
        Long taskId,
        Long userId,
        String status
    ) {
        private static final List<String> VALID_STATUSES = List.of("IN_PROGRESS", "COMPLETED", "CANCELLED");

        public UpdateStatusCommand {
            if (taskId == null || taskId <= 0) {
                throw new IllegalArgumentException("Task ID is required");
            }
            if (userId == null || userId <= 0) {
                throw new IllegalArgumentException("User ID is required");
            }
            if (status == null || status.isBlank()) {
                throw new IllegalArgumentException("Status is required");
            }
            if (!VALID_STATUSES.contains(status)) {
                throw new IllegalArgumentException("Invalid status: " + status + ". Must be one of: " + VALID_STATUSES);
            }
        }
    }

    record TaskResult(
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

    record UserInfo(Long id, String email, String name) {}
}
