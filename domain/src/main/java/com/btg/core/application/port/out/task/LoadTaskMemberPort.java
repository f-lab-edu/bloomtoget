package com.btg.core.application.port.out.task;

import java.util.List;
import java.util.Optional;

public interface LoadTaskMemberPort {

    Optional<TaskMember> loadByTaskIdAndUserId(Long taskId, Long userId);

    List<TaskMember> loadByTaskId(Long taskId);

    int countByTaskId(Long taskId);

    boolean existsByTaskIdAndUserId(Long taskId, Long userId);

    record TaskMember(
        Long id,
        Long taskId,
        Long userId,
        Long joinedAt
    ) {}
}
