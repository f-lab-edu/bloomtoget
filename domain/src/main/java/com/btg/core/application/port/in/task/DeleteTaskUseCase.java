package com.btg.core.application.port.in.task;

public interface DeleteTaskUseCase {
    void deleteTask(DeleteTaskCommand command);

    record DeleteTaskCommand(
        Long taskId,
        Long userId
    ) {
        public DeleteTaskCommand {
            // Self-validation
            if (taskId == null || taskId <= 0) {
                throw new IllegalArgumentException("Task ID is required");
            }
            if (userId == null || userId <= 0) {
                throw new IllegalArgumentException("User ID is required");
            }
            // TODO: Implement business logic in Service layer
            // - Only RECRUITING status can be deleted
            // - Only task creator or group ADMIN can delete
        }
    }
}
