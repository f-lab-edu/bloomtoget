package com.btg.core.application.service.task;

import com.btg.core.application.port.in.task.DeleteTaskUseCase;
import com.btg.core.application.port.out.dailyprogress.DeleteDailyProgressPort;
import com.btg.core.application.port.out.group.LoadGroupMemberPort;
import com.btg.core.application.port.out.task.DeleteTaskMemberPort;
import com.btg.core.application.port.out.task.DeleteTaskPort;
import com.btg.core.application.port.out.task.LoadTaskPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DeleteTaskService implements DeleteTaskUseCase {

    private static final String TASK_STATUS_RECRUITING = "RECRUITING";
    private static final String GROUP_ROLE_ADMIN = "ADMIN";

    private final LoadTaskPort loadTaskPort;
    private final LoadGroupMemberPort loadGroupMemberPort;
    private final DeleteDailyProgressPort deleteDailyProgressPort;
    private final DeleteTaskMemberPort deleteTaskMemberPort;
    private final DeleteTaskPort deleteTaskPort;

    @Override
    public void deleteTask(DeleteTaskCommand command) {
        // 1. Task 조회 및 존재 확인
        LoadTaskPort.Task task = loadTaskPort.loadById(command.taskId())
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + command.taskId()));

        // 2. Task 상태가 RECRUITING인지 확인
        if (!TASK_STATUS_RECRUITING.equals(task.status())) {
            throw new IllegalStateException("Only RECRUITING tasks can be deleted. Current status: " + task.status());
        }

        // 3. 권한 확인: Task 생성자이거나 Group ADMIN
        boolean isCreator = task.createdByUserId().equals(command.userId());
        boolean isGroupAdmin = loadGroupMemberPort.loadByGroupIdAndUserId(task.groupId(), command.userId())
                .map(member -> GROUP_ROLE_ADMIN.equals(member.role()))
                .orElse(false);

        if (!isCreator && !isGroupAdmin) {
            throw new IllegalArgumentException("Only task creator or group admin can delete the task");
        }

        // 4. DailyProgress 삭제 (TaskMember에 종속)
        deleteDailyProgressPort.deleteAllByTaskId(command.taskId());

        // 5. TaskMember 삭제
        deleteTaskMemberPort.deleteAllByTaskId(command.taskId());

        // 6. Task 삭제
        deleteTaskPort.deleteById(command.taskId());
    }
}