package com.btg.core.application.port.out.user;

import java.util.Optional;

public interface LoadUserPort {

    Optional<User> loadByEmail(String email);

    Optional<User> loadById(Long id);

    boolean existsByEmail(String email);

    record User(
            Long id,
            String email,
            String password,
            String name,
            Long createdAt
    ) {}
}
