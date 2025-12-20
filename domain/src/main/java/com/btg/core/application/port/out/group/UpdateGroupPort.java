package com.btg.core.application.port.out.group;

public interface UpdateGroupPort {
    Group update(Long groupId, String name, String description, Integer maxMembers);

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
