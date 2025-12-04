package com.btg.core.application.port.out.group;

public interface SaveGroupPort {
    Group save(String name, String description, Integer maxMembers, Long createdBy);

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
