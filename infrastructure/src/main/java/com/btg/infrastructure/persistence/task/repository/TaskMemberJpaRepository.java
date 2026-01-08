package com.btg.infrastructure.persistence.task.repository;

import com.btg.infrastructure.persistence.task.entity.TaskMemberJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskMemberJpaRepository extends JpaRepository<TaskMemberJpaEntity, Long> {

    Optional<TaskMemberJpaEntity> findByTaskIdAndUserId(Long taskId, Long userId);

    List<TaskMemberJpaEntity> findByTaskIdOrderByJoinedAtAsc(Long taskId);

    int countByTaskId(Long taskId);

    boolean existsByTaskIdAndUserId(Long taskId, Long userId);

    void deleteByTaskIdAndUserId(Long taskId, Long userId);

    void deleteByTaskId(Long taskId);
}
