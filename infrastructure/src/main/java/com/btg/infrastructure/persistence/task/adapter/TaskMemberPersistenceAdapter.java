package com.btg.infrastructure.persistence.task.adapter;

import com.btg.core.application.port.out.task.DeleteTaskMemberPort;
import com.btg.core.application.port.out.task.LoadTaskMemberPort;
import com.btg.core.application.port.out.task.SaveTaskMemberPort;
import com.btg.infrastructure.persistence.task.entity.TaskMemberJpaEntity;
import com.btg.infrastructure.persistence.task.repository.TaskMemberJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TaskMemberPersistenceAdapter implements
        SaveTaskMemberPort, LoadTaskMemberPort, DeleteTaskMemberPort {

    private final TaskMemberJpaRepository taskMemberRepository;

    @Override
    public SaveTaskMemberPort.TaskMember save(Long taskId, Long userId) {
        TaskMemberJpaEntity entity = new TaskMemberJpaEntity(taskId, userId);
        TaskMemberJpaEntity saved = taskMemberRepository.save(entity);

        return new SaveTaskMemberPort.TaskMember(
            saved.getId(),
            saved.getTaskId(),
            saved.getUserId(),
            saved.getJoinedAt().toInstant(ZoneOffset.UTC).toEpochMilli()
        );
    }

    @Override
    public Optional<LoadTaskMemberPort.TaskMember> loadByTaskIdAndUserId(Long taskId, Long userId) {
        return taskMemberRepository.findByTaskIdAndUserId(taskId, userId)
            .map(this::toTaskMember);
    }

    @Override
    public List<LoadTaskMemberPort.TaskMember> loadByTaskId(Long taskId) {
        return taskMemberRepository.findByTaskIdOrderByJoinedAtAsc(taskId).stream()
            .map(this::toTaskMember)
            .collect(Collectors.toList());
    }

    @Override
    public int countByTaskId(Long taskId) {
        return taskMemberRepository.countByTaskId(taskId);
    }

    @Override
    public boolean existsByTaskIdAndUserId(Long taskId, Long userId) {
        return taskMemberRepository.existsByTaskIdAndUserId(taskId, userId);
    }

    @Override
    @Transactional
    public void deleteByTaskIdAndUserId(Long taskId, Long userId) {
        taskMemberRepository.deleteByTaskIdAndUserId(taskId, userId);
    }

    @Override
    @Transactional
    public void deleteAllByTaskId(Long taskId) {
        taskMemberRepository.deleteByTaskId(taskId);
    }

    private LoadTaskMemberPort.TaskMember toTaskMember(TaskMemberJpaEntity entity) {
        return new LoadTaskMemberPort.TaskMember(
            entity.getId(),
            entity.getTaskId(),
            entity.getUserId(),
            entity.getJoinedAt().toInstant(ZoneOffset.UTC).toEpochMilli()
        );
    }
}
