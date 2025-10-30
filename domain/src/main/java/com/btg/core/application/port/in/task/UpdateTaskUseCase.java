package com.btg.core.application.port.in.task;

public interface UpdateTaskUseCase {
    TaskResult updateTask(UpdateTaskCommand command);

    record UpdateTaskCommand(
        Long taskId,
        Long userId,
        String title,
        String description,
        Integer maxParticipants
    ) {
        public UpdateTaskCommand {
            // Self-validation
            if (taskId == null || taskId <= 0) {
                throw new IllegalArgumentException("Task ID is required");
            }
            if (userId == null || userId <= 0) {
                throw new IllegalArgumentException("User ID is required");
            }
            // TODO: Implement authorization check in Service layer
            // Only task creator or group ADMIN can update

            // Optional: title
            if (title != null && !title.isBlank()) {
                if (title.length() < 2 || title.length() > 200) {
                    throw new IllegalArgumentException("Title must be between 2 and 200 characters");
                }
            }
            // Optional: description
            if (description != null && !description.isBlank()) {
                if (description.length() > 1000) {
                    throw new IllegalArgumentException("Description must not exceed 1000 characters");
                }
            }
            // Optional: maxParticipants
            if (maxParticipants != null) {
                if (maxParticipants < 1 || maxParticipants > 100) {
                    throw new IllegalArgumentException("Max participants must be between 1 and 100");
                }
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

    record UserInfo(
        Long id,
        String email,
        String name
    ) {}
}
