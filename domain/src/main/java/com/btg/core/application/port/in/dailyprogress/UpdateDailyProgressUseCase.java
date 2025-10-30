package com.btg.core.application.port.in.dailyprogress;

public interface UpdateDailyProgressUseCase {
    DailyProgressResult updateDailyProgress(UpdateDailyProgressCommand command);

    record UpdateDailyProgressCommand(
        Long taskId,
        Long userId,
        String date,  // Format: "YYYY-MM-DD"
        Boolean completed
    ) {
        public UpdateDailyProgressCommand {
            // Self-validation
            if (taskId == null || taskId <= 0) {
                throw new IllegalArgumentException("Task ID is required");
            }
            if (userId == null || userId <= 0) {
                throw new IllegalArgumentException("User ID is required");
            }
            if (date == null || date.isBlank()) {
                throw new IllegalArgumentException("Date is required");
            }
            // TODO: Validate date format in Service layer
            if (completed == null) {
                throw new IllegalArgumentException("Completed status is required");
            }
            // TODO: Implement business logic in Service layer
            // - Check if task status is not RECRUITING
            // - Check if date is within task date range
            // - Auto-set completedAt when completed=true
        }
    }

    record DailyProgressResult(
        String date,
        Boolean completed,
        String completedAt
    ) {}
}
