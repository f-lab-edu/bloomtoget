package com.btg.core.application.service.task;

import com.btg.core.application.port.in.task.ListTaskMembersUseCase;
import com.btg.core.application.port.out.dailyprogress.LoadDailyProgressPort;
import com.btg.core.application.port.out.task.LoadTaskMemberPort;
import com.btg.core.application.port.out.task.LoadTaskPort;
import com.btg.core.application.port.out.user.LoadUserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListTaskMembersService implements ListTaskMembersUseCase {

    private final LoadTaskPort loadTaskPort;
    private final LoadTaskMemberPort loadTaskMemberPort;
    private final LoadDailyProgressPort loadDailyProgressPort;
    private final LoadUserPort loadUserPort;

    @Override
    public TaskMemberListResult listTaskMembers(ListTaskMembersQuery query) {
        LoadTaskPort.Task task = loadTaskPort.loadById(query.taskId())
            .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        List<LoadTaskMemberPort.TaskMember> members = loadTaskMemberPort.loadByTaskId(query.taskId());

        List<TaskMemberInfo> memberInfos = members.stream()
            .map(member -> buildTaskMemberInfo(member, task.totalDays()))
            .collect(Collectors.toList());

        double avgRate = memberInfos.stream()
            .mapToDouble(TaskMemberInfo::completionRate)
            .average()
            .orElse(0.0);

        return new TaskMemberListResult(memberInfos, memberInfos.size(), avgRate);
    }

    private TaskMemberInfo buildTaskMemberInfo(LoadTaskMemberPort.TaskMember member, Integer totalDays) {
        LoadUserPort.User user = loadUserPort.loadById(member.userId()).orElse(null);

        int completedDays = loadDailyProgressPort.countCompletedByTaskMemberId(member.id());
        double completionRate = totalDays > 0
            ? (double) completedDays / totalDays * 100 : 0.0;

        UserInfo userInfo = user != null
            ? new UserInfo(user.id(), user.email(), user.name())
            : new UserInfo(member.userId(), "unknown", "Unknown");

        return new TaskMemberInfo(
            member.id(),
            userInfo,
            completionRate,
            completedDays,
            totalDays,
            member.joinedAt()
        );
    }
}
