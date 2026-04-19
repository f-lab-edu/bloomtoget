package com.btg.core.application.port.in.task;

public interface LeaveTaskUseCase {

    void leaveTask(LeaveTaskCommand command);

    record LeaveTaskCommand(
        Long taskId,
        Long userId
    ) {
        public LeaveTaskCommand {
            if (taskId == null || taskId <= 0) {
                throw new IllegalArgumentException("Task ID is required");
            }
            if (userId == null || userId <= 0) {
                throw new IllegalArgumentException("User ID is required");
            }
        }
    }
}
