package com.btg.core.application.service.task;

import com.btg.core.application.port.in.task.UpdateTaskStatusUseCase;
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
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateTaskStatusService implements UpdateTaskStatusUseCase {

    private final LoadTaskPort loadTaskPort;
    private final UpdateTaskPort updateTaskPort;
    private final LoadGroupMemberPort loadGroupMemberPort;
    private final LoadTaskMemberPort loadTaskMemberPort;
    private final LoadDailyProgressPort loadDailyProgressPort;
    private final LoadUserPort loadUserPort;

    private static final Map<String, Set<String>> VALID_TRANSITIONS = Map.of(
        "RECRUITING", Set.of("IN_PROGRESS", "CANCELLED"),
        "IN_PROGRESS", Set.of("COMPLETED", "CANCELLED")
    );

    @Override
    public TaskResult updateStatus(UpdateStatusCommand command) {
        LoadTaskPort.Task task = loadTaskPort.loadById(command.taskId())
            .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        validatePermission(task, command.userId());
        validateStateTransition(task.status(), command.status());

        UpdateTaskPort.Task updated = updateTaskPort.updateStatus(command.taskId(), command.status());

        return buildTaskResult(updated);
    }

    private void validatePermission(LoadTaskPort.Task task, Long userId) {
        boolean isCreator = task.createdByUserId().equals(userId);

        boolean isGroupAdmin = loadGroupMemberPort.loadByGroupIdAndUserId(task.groupId(), userId)
            .map(member -> "ADMIN".equals(member.role()))
            .orElse(false);

        if (!isCreator && !isGroupAdmin) {
            throw new IllegalArgumentException("Permission denied: only creator or group admin can change task status");
        }
    }

    private void validateStateTransition(String currentStatus, String newStatus) {
        Set<String> allowedTransitions = VALID_TRANSITIONS.get(currentStatus);

        if (allowedTransitions == null) {
            throw new IllegalStateException("Cannot change status from " + currentStatus);
        }

        if (!allowedTransitions.contains(newStatus)) {
            throw new IllegalStateException(
                "Invalid state transition: " + currentStatus + " -> " + newStatus +
                ". Allowed transitions: " + allowedTransitions
            );
        }
    }

    private TaskResult buildTaskResult(UpdateTaskPort.Task task) {
        int participantCount = loadTaskMemberPort.countByTaskId(task.id());

        int totalCompleted = loadDailyProgressPort.countCompletedByTaskId(task.id());
        int totalRecords = loadDailyProgressPort.countTotalByTaskId(task.id());
        double overallCompletionRate = totalRecords > 0
            ? (double) totalCompleted / totalRecords * 100 : 0.0;

        LoadUserPort.User creator = loadUserPort.loadById(task.createdByUserId())
            .orElse(null);

        UserInfo createdBy = creator != null
            ? new UserInfo(creator.id(), creator.email(), creator.name())
            : new UserInfo(task.createdByUserId(), "unknown", "Unknown");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

        return new TaskResult(
            task.id(),
            task.groupId(),
            task.title(),
            task.description(),
            task.status(),
            task.startDate(),
            task.endDate(),
            task.totalDays(),
            participantCount,
            task.maxParticipants(),
            overallCompletionRate,
            createdBy,
            Instant.ofEpochMilli(task.createdAt()).atOffset(ZoneOffset.UTC).format(formatter),
            Instant.ofEpochMilli(task.updatedAt()).atOffset(ZoneOffset.UTC).format(formatter)
        );
    }
}
