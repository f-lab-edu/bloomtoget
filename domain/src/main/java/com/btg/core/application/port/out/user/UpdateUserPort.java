package com.btg.core.application.port.out.user;

public interface UpdateUserPort {

    User update(Long userId, String name, String password);

    record User(
            Long id,
            String email,
            String name,
            String createdAt
    ) {}
}
