package com.btg.infrastructure.persistence.group.adapter;

import com.btg.core.application.port.out.group.DeleteGroupMemberPort;
import com.btg.core.application.port.out.group.LoadGroupMemberPort;
import com.btg.core.application.port.out.group.SaveGroupMemberPort;
import com.btg.core.application.port.out.group.UpdateGroupMemberPort;
import com.btg.infrastructure.persistence.group.entity.GroupMemberJpaEntity;
import com.btg.infrastructure.persistence.group.repository.GroupMemberJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GroupMemberPersistenceAdapter implements
        SaveGroupMemberPort, LoadGroupMemberPort, DeleteGroupMemberPort, UpdateGroupMemberPort {

    private final GroupMemberJpaRepository groupMemberRepository;

    @Override
    public SaveGroupMemberPort.GroupMember save(Long groupId, Long userId, String role) {
        GroupMemberJpaEntity entity = new GroupMemberJpaEntity(groupId, userId, role);
        GroupMemberJpaEntity saved = groupMemberRepository.save(entity);
        return new SaveGroupMemberPort.GroupMember(
                saved.getId(),
                saved.getGroupId(),
                saved.getUserId(),
                saved.getRole(),
                saved.getJoinedAt().toInstant(ZoneOffset.UTC).toEpochMilli()
        );
    }

    @Override
    public Optional<LoadGroupMemberPort.GroupMember> loadByGroupIdAndUserId(Long groupId, Long userId) {
        return groupMemberRepository.findByGroupIdAndUserId(groupId, userId)
                .map(this::toGroupMember);
    }

    @Override
    public int countByGroupId(Long groupId) {
        return groupMemberRepository.countByGroupId(groupId);
    }

    @Override
    public boolean existsByGroupIdAndUserId(Long groupId, Long userId) {
        return groupMemberRepository.existsByGroupIdAndUserId(groupId, userId);
    }

    @Override
    public void deleteByGroupId(Long groupId) {
        groupMemberRepository.deleteByGroupId(groupId);
    }

    @Override
    public void deleteByGroupIdAndUserId(Long groupId, Long userId) {
        groupMemberRepository.deleteByGroupIdAndUserId(groupId, userId);
    }

    @Override
    public List<LoadGroupMemberPort.GroupMember> loadByGroupId(Long groupId) {
        return groupMemberRepository.findByGroupIdOrderByJoinedAtAsc(groupId).stream()
                .map(this::toGroupMember)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<LoadGroupMemberPort.GroupMember> loadOldestMemberByGroupId(Long groupId, Long excludeUserId) {
        return groupMemberRepository.findFirstByGroupIdAndRoleAndUserIdNotOrderByJoinedAtAsc(groupId, "MEMBER", excludeUserId)
                .map(this::toGroupMember);
    }

    @Override
    public void updateRole(Long groupMemberId, String newRole) {
        GroupMemberJpaEntity entity = groupMemberRepository.findById(groupMemberId)
                .orElseThrow(() -> new IllegalArgumentException("GroupMember not found: " + groupMemberId));
        entity.updateRole(newRole);
        groupMemberRepository.save(entity);
    }

    private LoadGroupMemberPort.GroupMember toGroupMember(GroupMemberJpaEntity entity) {
        return new LoadGroupMemberPort.GroupMember(
                entity.getId(),
                entity.getGroupId(),
                entity.getUserId(),
                entity.getRole(),
                entity.getJoinedAt().toInstant(ZoneOffset.UTC).toEpochMilli()
        );
    }
}
