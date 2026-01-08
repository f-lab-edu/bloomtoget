package com.btg.core.application.port.in.task;

import java.util.List;

public interface ListTaskMembersUseCase {

    TaskMemberListResult listTaskMembers(ListTaskMembersQuery query);

    record ListTaskMembersQuery(Long taskId) {
        public ListTaskMembersQuery {
            if (taskId == null || taskId <= 0) {
                throw new IllegalArgumentException("Task ID is required");
            }
        }
    }

    record TaskMemberListResult(
        List<TaskMemberInfo> members,
        Integer totalCount,
        Double averageCompletionRate
    ) {}

    record TaskMemberInfo(
        Long id,
        UserInfo user,
        Double completionRate,
        Integer completedDays,
        Integer totalDays,
        Long joinedAt
    ) {}

    record UserInfo(Long id, String email, String name) {}
}
