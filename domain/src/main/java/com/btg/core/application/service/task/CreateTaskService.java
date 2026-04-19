package com.btg.core.application.service.task;

import com.btg.core.application.port.in.task.CreateTaskUseCase;
import com.btg.core.application.port.out.group.LoadGroupMemberPort;
import com.btg.core.application.port.out.group.LoadGroupPort;
import com.btg.core.application.port.out.task.SaveTaskMemberPort;
import com.btg.core.application.port.out.task.SaveTaskPort;
import com.btg.core.application.port.out.user.LoadUserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateTaskService implements CreateTaskUseCase {

    private static final String TASK_STATUS_RECRUITING = "RECRUITING";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    private final LoadUserPort loadUserPort;
    private final LoadGroupPort loadGroupPort;
    private final LoadGroupMemberPort loadGroupMemberPort;
    private final SaveTaskPort saveTaskPort;
    private final SaveTaskMemberPort saveTaskMemberPort;

    @Override
    public TaskResult createTask(CreateTaskCommand command) {
        LoadUserPort.User user = loadUserPort.loadById(command.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + command.userId()));

        loadGroupPort.loadById(command.groupId())
                .orElseThrow(() -> new IllegalArgumentException("Group not found: " + command.groupId()));

        boolean isMember = loadGroupMemberPort.existsByGroupIdAndUserId(command.groupId(), command.userId());
        if (!isMember) {
            throw new IllegalArgumentException("User is not a member of the group");
        }

        LocalDate startDate = parseDate(command.startDate(), "Invalid start date format");
        LocalDate endDate = parseDate(command.endDate(), "Invalid end date format");

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before or equal to end date");
        }

        int totalDays = (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;

        SaveTaskPort.Task savedTask = saveTaskPort.save(
                command.groupId(),
                command.userId(),
                command.title(),
                command.description(),
                TASK_STATUS_RECRUITING,
                command.startDate(),
                command.endDate(),
                totalDays,
                command.maxParticipants()
        );

        saveTaskMemberPort.save(savedTask.id(), command.userId());

        return new TaskResult(
                savedTask.id(),
                savedTask.groupId(),
                savedTask.title(),
                savedTask.description(),
                savedTask.status(),
                savedTask.startDate(),
                savedTask.endDate(),
                savedTask.totalDays(),
                1,
                savedTask.maxParticipants(),
                0.0,
                new UserInfo(user.id(), user.email(), user.name()),
                String.valueOf(savedTask.createdAt()),
                String.valueOf(savedTask.updatedAt())
        );
    }

    private LocalDate parseDate(String dateStr, String errorMessage) {
        try {
            return LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(errorMessage + ": " + dateStr);
        }
    }
}