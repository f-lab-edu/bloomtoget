package com.btg.infrastructure.persistence.user.adapter;

import com.btg.core.application.port.out.user.LoadUserPort;
import com.btg.core.application.port.out.user.SaveUserPort;
import com.btg.core.application.port.out.user.UpdateUserPort;
import com.btg.infrastructure.persistence.user.entity.UserJpaEntity;
import com.btg.infrastructure.persistence.user.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements LoadUserPort, SaveUserPort, UpdateUserPort {

    private final UserJpaRepository userJpaRepository;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public Optional<LoadUserPort.User> loadByEmail(String email) {
        return userJpaRepository.findByEmail(email)
                .map(this::toLoadUser);
    }

    @Override
    public Optional<LoadUserPort.User> loadById(Long id) {
        return userJpaRepository.findById(id)
                .map(this::toLoadUser);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    public SaveUserPort.User save(String email, String password, String name) {
        UserJpaEntity entity = new UserJpaEntity(email, password, name);
        UserJpaEntity saved = userJpaRepository.save(entity);
        return new SaveUserPort.User(
                saved.getId(),
                saved.getEmail(),
                saved.getName(),
                saved.getCreatedAt().format(FORMATTER)
        );
    }

    @Override
    public UpdateUserPort.User update(Long userId, String name, String password) {
        UserJpaEntity entity = userJpaRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        if (name != null && !name.isBlank()) {
            entity.updateName(name);
        }
        if (password != null && !password.isBlank()) {
            entity.updatePassword(password);
        }

        UserJpaEntity updated = userJpaRepository.save(entity);
        return new UpdateUserPort.User(
                updated.getId(),
                updated.getEmail(),
                updated.getName(),
                updated.getCreatedAt().format(FORMATTER)
        );
    }

    private LoadUserPort.User toLoadUser(UserJpaEntity entity) {
        return new LoadUserPort.User(
                entity.getId(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getName(),
                entity.getCreatedAt().format(FORMATTER)
        );
    }
}
