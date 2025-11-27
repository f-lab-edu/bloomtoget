package com.btg.core.application.port.out.user;

public interface SaveUserPort {

    User save(String email, String password, String name);

    record User(
            Long id,
            String email,
            String name,
            String createdAt
    ) {}
}
