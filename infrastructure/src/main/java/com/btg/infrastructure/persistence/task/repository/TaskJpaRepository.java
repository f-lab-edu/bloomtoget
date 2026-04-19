package com.btg.infrastructure.persistence.task.repository;

import com.btg.infrastructure.persistence.task.entity.TaskJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskJpaRepository extends JpaRepository<TaskJpaEntity, Long> {

    Optional<TaskJpaEntity> findById(Long id);

    Page<TaskJpaEntity> findByGroupId(Long groupId, Pageable pageable);

    Page<TaskJpaEntity> findByGroupIdAndStatus(Long groupId, String status, Pageable pageable);
}
