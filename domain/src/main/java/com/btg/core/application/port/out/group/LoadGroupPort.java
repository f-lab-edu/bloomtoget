package com.btg.core.application.port.out.group;

import java.util.List;
import java.util.Optional;

public interface LoadGroupPort {
    Optional<Group> loadById(Long groupId);
    List<Group> loadByUserId(Long userId, Integer page, Integer size);
    List<Group> loadAll(Integer page, Integer size);
    List<Group> searchByKeyword(String keyword, Integer page, Integer size);
    int countByUserId(Long userId);
    int countAll();
    int countByKeyword(String keyword);

    record Group(
        Long id,
        String name,
        String description,
        Integer memberCount,
        Integer maxMembers,
        Long createdBy,
        Long createdAt
    ) {}
}
