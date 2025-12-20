package com.btg.infrastructure.persistence.group.adapter;

import com.btg.core.application.port.out.group.*;
import com.btg.infrastructure.persistence.group.entity.GroupJpaEntity;
import com.btg.infrastructure.persistence.group.repository.GroupJpaRepository;
import com.btg.infrastructure.persistence.group.repository.GroupMemberJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GroupPersistenceAdapter implements
        SaveGroupPort, LoadGroupPort, UpdateGroupPort, DeleteGroupPort {

    private final GroupJpaRepository groupRepository;
    private final GroupMemberJpaRepository groupMemberRepository;

    @Override
    public SaveGroupPort.Group save(String name, String description, Integer maxMembers, Long createdBy) {
        GroupJpaEntity entity = new GroupJpaEntity(name, description, maxMembers, createdBy);
        GroupJpaEntity saved = groupRepository.save(entity);
        return new SaveGroupPort.Group(
                saved.getId(),
                saved.getName(),
                saved.getDescription(),
                0,
                saved.getMaxMembers(),
                saved.getCreatedBy(),
                saved.getCreatedAt().toInstant(ZoneOffset.UTC).toEpochMilli()
        );
    }

    @Override
    public Optional<LoadGroupPort.Group> loadById(Long groupId) {
        return groupRepository.findById(groupId)
                .map(this::toLoadGroup);
    }

    @Override
    public List<LoadGroupPort.Group> loadByUserId(Long userId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return groupRepository.findByUserId(userId, pageable).stream()
                .map(this::toLoadGroup)
                .collect(Collectors.toList());
    }

    @Override
    public List<LoadGroupPort.Group> loadAll(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return groupRepository.findAllByOrderByCreatedAtDesc(pageable).stream()
                .map(this::toLoadGroup)
                .collect(Collectors.toList());
    }

    @Override
    public int countByUserId(Long userId) {
        return groupRepository.countByUserId(userId);
    }

    @Override
    public int countAll() {
        return (int) groupRepository.count();
    }

    @Override
    public List<LoadGroupPort.Group> searchByKeyword(String keyword, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return groupRepository.searchByKeyword(keyword, pageable).stream()
                .map(this::toLoadGroup)
                .collect(Collectors.toList());
    }

    @Override
    public int countByKeyword(String keyword) {
        return groupRepository.countByKeyword(keyword);
    }

    @Override
    public UpdateGroupPort.Group update(Long groupId, String name, String description, Integer maxMembers) {
        GroupJpaEntity entity = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found: " + groupId));

        entity.update(name, description, maxMembers);
        GroupJpaEntity updated = groupRepository.save(entity);
        return new UpdateGroupPort.Group(
                updated.getId(),
                updated.getName(),
                updated.getDescription(),
                getMemberCount(updated.getId()),
                updated.getMaxMembers(),
                updated.getCreatedBy(),
                updated.getCreatedAt().toInstant(ZoneOffset.UTC).toEpochMilli()
        );
    }

    @Override
    public void delete(Long groupId) {
        groupMemberRepository.deleteByGroupId(groupId);
        groupRepository.deleteById(groupId);
    }

    private LoadGroupPort.Group toLoadGroup(GroupJpaEntity entity) {
        return new LoadGroupPort.Group(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                getMemberCount(entity.getId()),
                entity.getMaxMembers(),
                entity.getCreatedBy(),
                entity.getCreatedAt().toInstant(ZoneOffset.UTC).toEpochMilli()
        );
    }

    private int getMemberCount(Long groupId) {
        return groupMemberRepository.countByGroupId(groupId);
    }
}
