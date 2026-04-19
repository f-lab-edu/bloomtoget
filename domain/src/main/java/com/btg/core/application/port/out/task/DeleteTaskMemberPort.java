package com.btg.core.application.port.out.task;

public interface DeleteTaskMemberPort {

    void deleteByTaskIdAndUserId(Long taskId, Long userId);

    void deleteAllByTaskId(Long taskId);
}
