package com.btg.core.application.port.in.task;

public interface JoinTaskUseCase {

    TaskMemberResult joinTask(JoinTaskCommand command);

    record JoinTaskCommand(
        Long taskId,
        Long userId
    ) {
        public JoinTaskCommand {
            if (taskId == null || taskId <= 0) {
                throw new IllegalArgumentException("Task ID is required");
            }
            if (userId == null || userId <= 0) {
                throw new IllegalArgumentException("User ID is required");
            }
        }
    }

    record TaskMemberResult(
        Long id,
        UserInfo user,
        Double completionRate,
        Integer completedDays,
        Integer totalDays,
        Long joinedAt
    ) {}

    record UserInfo(Long id, String email, String name) {}
}
