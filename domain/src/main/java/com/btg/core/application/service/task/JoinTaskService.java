package com.btg.core.application.service.task;

import com.btg.core.application.port.in.task.JoinTaskUseCase;
import com.btg.core.application.port.out.dailyprogress.SaveDailyProgressPort;
import com.btg.core.application.port.out.group.LoadGroupMemberPort;
import com.btg.core.application.port.out.task.LoadTaskMemberPort;
import com.btg.core.application.port.out.task.LoadTaskPort;
import com.btg.core.application.port.out.task.SaveTaskMemberPort;
import com.btg.core.application.port.out.user.LoadUserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class JoinTaskService implements JoinTaskUseCase {

    private final LoadTaskPort loadTaskPort;
    private final LoadGroupMemberPort loadGroupMemberPort;
    private final LoadTaskMemberPort loadTaskMemberPort;
    private final SaveTaskMemberPort saveTaskMemberPort;
    private final SaveDailyProgressPort saveDailyProgressPort;
    private final LoadUserPort loadUserPort;

    @Override
    public TaskMemberResult joinTask(JoinTaskCommand command) {
        LoadTaskPort.Task task = loadTaskPort.loadById(command.taskId())
            .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        if (!loadGroupMemberPort.existsByGroupIdAndUserId(task.groupId(), command.userId())) {
            throw new IllegalArgumentException("User is not a member of the group");
        }

        if (loadTaskMemberPort.existsByTaskIdAndUserId(command.taskId(), command.userId())) {
            throw new IllegalArgumentException("Already participating in this task");
        }

        if (!"RECRUITING".equals(task.status())) {
            throw new IllegalArgumentException("Cannot join task in " + task.status() + " status");
        }

        if (task.maxParticipants() != null) {
            int currentCount = loadTaskMemberPort.countByTaskId(command.taskId());
            if (currentCount >= task.maxParticipants()) {
                throw new IllegalArgumentException("Task is full");
            }
        }

        SaveTaskMemberPort.TaskMember savedMember = saveTaskMemberPort.save(
            command.taskId(), command.userId());

        List<SaveDailyProgressPort.DailyProgressEntry> entries = generateDailyProgressEntries(
            task.startDate(), task.endDate());
        saveDailyProgressPort.saveAll(savedMember.id(), entries);

        LoadUserPort.User user = loadUserPort.loadById(command.userId())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return new TaskMemberResult(
            savedMember.id(),
            new UserInfo(user.id(), user.email(), user.name()),
            0.0,
            0,
            task.totalDays(),
            savedMember.joinedAt()
        );
    }

    private List<SaveDailyProgressPort.DailyProgressEntry> generateDailyProgressEntries(
            String startDate, String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        List<SaveDailyProgressPort.DailyProgressEntry> entries = new ArrayList<>();

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            entries.add(new SaveDailyProgressPort.DailyProgressEntry(date.toString(), false));
        }

        return entries;
    }
}
