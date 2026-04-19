package com.btg.core.application.service.task;

import com.btg.core.application.port.in.task.LeaveTaskUseCase;
import com.btg.core.application.port.out.task.DeleteTaskMemberPort;
import com.btg.core.application.port.out.task.LoadTaskMemberPort;
import com.btg.core.application.port.out.task.LoadTaskPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LeaveTaskService implements LeaveTaskUseCase {

    private final LoadTaskPort loadTaskPort;
    private final LoadTaskMemberPort loadTaskMemberPort;
    private final DeleteTaskMemberPort deleteTaskMemberPort;

    @Override
    public void leaveTask(LeaveTaskCommand command) {
        LoadTaskPort.Task task = loadTaskPort.loadById(command.taskId())
            .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        loadTaskMemberPort.loadByTaskIdAndUserId(command.taskId(), command.userId())
            .orElseThrow(() -> new IllegalArgumentException("User is not participating in this task"));

        if ("IN_PROGRESS".equals(task.status())) {
            throw new IllegalArgumentException("Cannot leave task in IN_PROGRESS status");
        }

        deleteTaskMemberPort.deleteByTaskIdAndUserId(command.taskId(), command.userId());
    }
}
