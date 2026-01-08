package com.btg.core.application.service.task;

import com.btg.core.application.port.in.task.UpdateTaskUseCase;
import com.btg.core.application.port.out.dailyprogress.LoadDailyProgressPort;
import com.btg.core.application.port.out.group.LoadGroupMemberPort;
import com.btg.core.application.port.out.task.LoadTaskMemberPort;
import com.btg.core.application.port.out.task.LoadTaskPort;
import com.btg.core.application.port.out.task.UpdateTaskPort;
import com.btg.core.application.port.out.user.LoadUserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateTaskService implements UpdateTaskUseCase {

    private static final String GROUP_ROLE_ADMIN = "ADMIN";
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_INSTANT;

    private final LoadTaskPort loadTaskPort;
    private final UpdateTaskPort updateTaskPort;
    private final LoadGroupMemberPort loadGroupMemberPort;
    private final LoadTaskMemberPort loadTaskMemberPort;
    private final LoadDailyProgressPort loadDailyProgressPort;
    private final LoadUserPort loadUserPort;

    @Override
    public TaskResult updateTask(UpdateTaskCommand command) {
        // 1. Task 조회 및 존재 확인
        LoadTaskPort.Task task = loadTaskPort.loadById(command.taskId())
            .orElseThrow(() -> new IllegalArgumentException("Task not found: " + command.taskId()));

        // 2. 권한 확인: Task 생성자이거나 Group ADMIN
        boolean isCreator = task.createdByUserId().equals(command.userId());
        boolean isGroupAdmin = loadGroupMemberPort.loadByGroupIdAndUserId(task.groupId(), command.userId())
            .map(member -> GROUP_ROLE_ADMIN.equals(member.role()))
            .orElse(false);

        if (!isCreator && !isGroupAdmin) {
            throw new IllegalArgumentException("Only task creator or group admin can update the task");
        }

        // 3. maxParticipants 변경 시 현재 참가자 수 확인
        if (command.maxParticipants() != null) {
            int currentParticipants = loadTaskMemberPort.countByTaskId(command.taskId());
            if (command.maxParticipants() < currentParticipants) {
                throw new IllegalStateException(
                    "Cannot reduce maxParticipants below current participant count: " + currentParticipants
                );
            }
        }

        // 4. Task 업데이트
        UpdateTaskPort.Task updatedTask = updateTaskPort.update(
            command.taskId(),
            command.title(),
            command.description(),
            command.maxParticipants()
        );

        // 5. 통계 계산
        int participantCount = loadTaskMemberPort.countByTaskId(command.taskId());
        int completedCount = loadDailyProgressPort.countCompletedByTaskId(command.taskId());
        int totalCount = loadDailyProgressPort.countTotalByTaskId(command.taskId());
        double overallCompletionRate = totalCount > 0 ? (double) completedCount / totalCount * 100 : 0.0;

        // 6. 생성자 정보 조회
        UserInfo createdBy = loadUserPort.loadById(updatedTask.createdByUserId())
            .map(user -> new UserInfo(user.id(), user.email(), user.name()))
            .orElse(new UserInfo(updatedTask.createdByUserId(), "unknown", "Unknown User"));

        return new TaskResult(
            updatedTask.id(),
            updatedTask.groupId(),
            updatedTask.title(),
            updatedTask.description(),
            updatedTask.status(),
            updatedTask.startDate(),
            updatedTask.endDate(),
            updatedTask.totalDays(),
            participantCount,
            updatedTask.maxParticipants(),
            Math.round(overallCompletionRate * 100.0) / 100.0,
            createdBy,
            formatTimestamp(updatedTask.createdAt()),
            formatTimestamp(updatedTask.updatedAt())
        );
    }

    private String formatTimestamp(Long epochMilli) {
        return Instant.ofEpochMilli(epochMilli)
            .atOffset(ZoneOffset.UTC)
            .format(ISO_FORMATTER);
    }
}
