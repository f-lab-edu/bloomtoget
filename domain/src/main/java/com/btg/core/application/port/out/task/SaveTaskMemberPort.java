package com.btg.core.application.port.out.task;

public interface SaveTaskMemberPort {

    TaskMember save(Long taskId, Long userId);

    record TaskMember(
        Long id,
        Long taskId,
        Long userId,
        Long joinedAt
    ) {}
}
