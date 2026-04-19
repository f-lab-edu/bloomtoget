package com.btg.core.application.service.task;

import com.btg.core.application.port.in.task.ListTasksUseCase;
import com.btg.core.application.port.out.dailyprogress.LoadDailyProgressPort;
import com.btg.core.application.port.out.task.LoadTaskMemberPort;
import com.btg.core.application.port.out.task.LoadTaskPort;
import com.btg.core.application.port.out.user.LoadUserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListTasksService implements ListTasksUseCase {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_INSTANT;

    private final LoadTaskPort loadTaskPort;
    private final LoadTaskMemberPort loadTaskMemberPort;
    private final LoadDailyProgressPort loadDailyProgressPort;
    private final LoadUserPort loadUserPort;

    @Override
    public PagedTaskResult listTasks(ListTasksQuery query) {
        int page = query.page() != null ? query.page() : DEFAULT_PAGE;
        int size = query.size() != null ? query.size() : DEFAULT_SIZE;
        String status = query.status() != null ? query.status() : "ALL";

        if (query.groupId() == null) {
            throw new IllegalArgumentException("Group ID is required for listing tasks");
        }

        LoadTaskPort.PagedTask pagedTask = loadTaskPort.loadByGroupId(
            query.groupId(),
            status,
            page,
            size
        );

        List<TaskSummary> taskSummaries = pagedTask.content().stream()
            .map(this::toTaskSummary)
            .toList();

        return new PagedTaskResult(
            taskSummaries,
            pagedTask.totalElements(),
            pagedTask.totalPages(),
            pagedTask.page(),
            pagedTask.size()
        );
    }

    private TaskSummary toTaskSummary(LoadTaskPort.Task task) {
        int participantCount = loadTaskMemberPort.countByTaskId(task.id());

        int completedCount = loadDailyProgressPort.countCompletedByTaskId(task.id());
        int totalCount = loadDailyProgressPort.countTotalByTaskId(task.id());
        double overallCompletionRate = totalCount > 0 ? (double) completedCount / totalCount * 100 : 0.0;

        UserInfo createdBy = loadUserPort.loadById(task.createdByUserId())
            .map(user -> new UserInfo(user.id(), user.email(), user.name()))
            .orElse(new UserInfo(task.createdByUserId(), "unknown", "Unknown User"));

        return new TaskSummary(
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
            Math.round(overallCompletionRate * 100.0) / 100.0,
            createdBy,
            formatTimestamp(task.createdAt()),
            formatTimestamp(task.updatedAt())
        );
    }

    private String formatTimestamp(Long epochMilli) {
        return Instant.ofEpochMilli(epochMilli)
            .atOffset(ZoneOffset.UTC)
            .format(ISO_FORMATTER);
    }
}
