package com.btg.core.application.port.in.task;

public interface CreateTaskUseCase {
    TaskResult createTask(CreateTaskCommand command);

    record CreateTaskCommand(
        Long userId,
        Long groupId,
        String title,
        String description,
        String startDate,  // Format: "YYYY-MM-DD"
        String endDate,    // Format: "YYYY-MM-DD"
        Integer maxParticipants
    ) {
        public CreateTaskCommand {
            // Self-validation
            if (userId == null || userId <= 0) {
                throw new IllegalArgumentException("User ID is required");
            }
            if (groupId == null || groupId <= 0) {
                throw new IllegalArgumentException("Group ID is required");
            }
            if (title == null || title.isBlank()) {
                throw new IllegalArgumentException("Title is required");
            }
            if (title.length() < 2 || title.length() > 200) {
                throw new IllegalArgumentException("Title must be between 2 and 200 characters");
            }
            if (startDate == null || startDate.isBlank()) {
                throw new IllegalArgumentException("Start date is required");
            }
            if (endDate == null || endDate.isBlank()) {
                throw new IllegalArgumentException("End date is required");
            }
            // TODO: Validate date format and startDate <= endDate in Service layer

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
        String status,  // "RECRUITING", "IN_PROGRESS", "COMPLETED", "CANCELLED"
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
