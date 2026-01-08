package com.btg.infrastructure.persistence.task.adapter;

import com.btg.core.application.port.out.task.DeleteTaskPort;
import com.btg.core.application.port.out.task.LoadTaskPort;
import com.btg.core.application.port.out.task.SaveTaskPort;
import com.btg.core.application.port.out.task.UpdateTaskPort;
import com.btg.infrastructure.persistence.task.entity.TaskJpaEntity;
import com.btg.infrastructure.persistence.task.repository.TaskJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TaskPersistenceAdapter implements LoadTaskPort, SaveTaskPort, UpdateTaskPort, DeleteTaskPort {

    private final TaskJpaRepository taskRepository;

    @Override
    public SaveTaskPort.Task save(Long groupId, Long createdByUserId, String title, String description,
                                   String status, String startDate, String endDate,
                                   Integer totalDays, Integer maxParticipants) {
        TaskJpaEntity entity = new TaskJpaEntity(
            groupId,
            createdByUserId,
            title,
            description,
            status,
            LocalDate.parse(startDate),
            LocalDate.parse(endDate),
            totalDays,
            maxParticipants
        );
        TaskJpaEntity saved = taskRepository.save(entity);
        return toSaveTaskPortTask(saved);
    }

    @Override
    public Optional<LoadTaskPort.Task> loadById(Long taskId) {
        return taskRepository.findById(taskId)
            .map(this::toLoadTaskPortTask);
    }

    @Override
    public PagedTask loadByGroupId(Long groupId, String status, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<TaskJpaEntity> taskPage;
        if (status == null || status.isBlank() || "ALL".equals(status)) {
            taskPage = taskRepository.findByGroupId(groupId, pageRequest);
        } else {
            taskPage = taskRepository.findByGroupIdAndStatus(groupId, status, pageRequest);
        }

        return new PagedTask(
            taskPage.getContent().stream().map(this::toLoadTaskPortTask).toList(),
            (int) taskPage.getTotalElements(),
            taskPage.getTotalPages(),
            taskPage.getNumber(),
            taskPage.getSize()
        );
    }

    @Override
    public UpdateTaskPort.Task updateStatus(Long taskId, String newStatus) {
        TaskJpaEntity entity = taskRepository.findById(taskId)
            .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));

        entity.updateStatus(newStatus);
        TaskJpaEntity saved = taskRepository.save(entity);

        return toUpdateTaskPortTask(saved);
    }

    @Override
    public UpdateTaskPort.Task update(Long taskId, String title, String description, Integer maxParticipants) {
        TaskJpaEntity entity = taskRepository.findById(taskId)
            .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));

        entity.update(title, description, maxParticipants);
        TaskJpaEntity saved = taskRepository.save(entity);

        return toUpdateTaskPortTask(saved);
    }

    @Override
    @Transactional
    public void deleteById(Long taskId) {
        taskRepository.deleteById(taskId);
    }

    private SaveTaskPort.Task toSaveTaskPortTask(TaskJpaEntity entity) {
        return new SaveTaskPort.Task(
            entity.getId(),
            entity.getGroupId(),
            entity.getCreatedByUserId(),
            entity.getTitle(),
            entity.getDescription(),
            entity.getStatus(),
            entity.getStartDate().toString(),
            entity.getEndDate().toString(),
            entity.getTotalDays(),
            entity.getMaxParticipants(),
            entity.getCreatedAt().toInstant(ZoneOffset.UTC).toEpochMilli(),
            entity.getUpdatedAt().toInstant(ZoneOffset.UTC).toEpochMilli()
        );
    }

    private LoadTaskPort.Task toLoadTaskPortTask(TaskJpaEntity entity) {
        return new LoadTaskPort.Task(
            entity.getId(),
            entity.getGroupId(),
            entity.getCreatedByUserId(),
            entity.getTitle(),
            entity.getDescription(),
            entity.getStatus(),
            entity.getStartDate().toString(),
            entity.getEndDate().toString(),
            entity.getTotalDays(),
            entity.getMaxParticipants(),
            entity.getCreatedAt().toInstant(ZoneOffset.UTC).toEpochMilli(),
            entity.getUpdatedAt().toInstant(ZoneOffset.UTC).toEpochMilli()
        );
    }

    private UpdateTaskPort.Task toUpdateTaskPortTask(TaskJpaEntity entity) {
        return new UpdateTaskPort.Task(
            entity.getId(),
            entity.getGroupId(),
            entity.getCreatedByUserId(),
            entity.getTitle(),
            entity.getDescription(),
            entity.getStatus(),
            entity.getStartDate().toString(),
            entity.getEndDate().toString(),
            entity.getTotalDays(),
            entity.getMaxParticipants(),
            entity.getCreatedAt().toInstant(ZoneOffset.UTC).toEpochMilli(),
            entity.getUpdatedAt().toInstant(ZoneOffset.UTC).toEpochMilli()
        );
    }
}
