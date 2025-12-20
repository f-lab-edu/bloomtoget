package com.btg.infrastructure.persistence.group.repository;

import com.btg.infrastructure.persistence.group.entity.GroupMemberJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupMemberJpaRepository extends JpaRepository<GroupMemberJpaEntity, Long> {
    Optional<GroupMemberJpaEntity> findByGroupIdAndUserId(Long groupId, Long userId);
    List<GroupMemberJpaEntity> findByGroupIdOrderByJoinedAtAsc(Long groupId);
    Optional<GroupMemberJpaEntity> findFirstByGroupIdAndRoleAndUserIdNotOrderByJoinedAtAsc(Long groupId, String role, Long excludeUserId);
    int countByGroupId(Long groupId);
    boolean existsByGroupIdAndUserId(Long groupId, Long userId);
    void deleteByGroupId(Long groupId);
    void deleteByGroupIdAndUserId(Long groupId, Long userId);
}
