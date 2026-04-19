package com.btg.core.application.service.task;

import com.btg.core.application.port.in.task.GetTaskProgressUseCase;
import com.btg.core.application.port.out.dailyprogress.LoadDailyProgressPort;
import com.btg.core.application.port.out.task.LoadTaskMemberPort;
import com.btg.core.application.port.out.task.LoadTaskPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetTaskProgressService implements GetTaskProgressUseCase {

    private final LoadTaskPort loadTaskPort;
    private final LoadTaskMemberPort loadTaskMemberPort;
    private final LoadDailyProgressPort loadDailyProgressPort;

    @Override
    public TaskProgressResult getTaskProgress(Long taskId) {
        LoadTaskPort.Task task = loadTaskPort.loadById(taskId)
            .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        int totalParticipants = loadTaskMemberPort.countByTaskId(taskId);

        int totalCompleted = loadDailyProgressPort.countCompletedByTaskId(taskId);
        int totalRecords = loadDailyProgressPort.countTotalByTaskId(taskId);

        double overallCompletionRate = totalRecords > 0
            ? (double) totalCompleted / totalRecords * 100 : 0.0;

        List<LoadTaskMemberPort.TaskMember> members = loadTaskMemberPort.loadByTaskId(taskId);
        double avgRate = members.stream()
            .mapToDouble(m -> {
                int completed = loadDailyProgressPort.countCompletedByTaskMemberId(m.id());
                return task.totalDays() > 0 ? (double) completed / task.totalDays() * 100 : 0.0;
            })
            .average()
            .orElse(0.0);

        return new TaskProgressResult(
            taskId,
            totalParticipants,
            task.totalDays(),
            overallCompletionRate,
            avgRate
        );
    }
}
