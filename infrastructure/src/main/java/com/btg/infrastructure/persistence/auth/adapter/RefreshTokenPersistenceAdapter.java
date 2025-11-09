package com.btg.infrastructure.persistence.auth.adapter;

import com.btg.core.application.port.out.auth.DeleteRefreshTokenPort;
import com.btg.core.application.port.out.auth.LoadRefreshTokenPort;
import com.btg.core.application.port.out.auth.SaveRefreshTokenPort;
import com.btg.infrastructure.persistence.auth.entity.RefreshTokenJpaEntity;
import com.btg.infrastructure.persistence.auth.repository.RefreshTokenJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RefreshTokenPersistenceAdapter implements
        SaveRefreshTokenPort, LoadRefreshTokenPort, DeleteRefreshTokenPort {

    private final RefreshTokenJpaRepository refreshTokenJpaRepository;

    @Override
    public void save(String token, Long userId, LocalDateTime expiresAt) {
        RefreshTokenJpaEntity entity = new RefreshTokenJpaEntity(token, userId, expiresAt);
        refreshTokenJpaRepository.save(entity);
    }

    @Override
    public Optional<RefreshToken> loadByToken(String token) {
        return refreshTokenJpaRepository.findByToken(token)
                .map(entity -> new RefreshToken(
                        entity.getId(),
                        entity.getToken(),
                        entity.getUserId(),
                        entity.getExpiresAt(),
                        entity.getCreatedAt()
                ));
    }

    @Override
    @Transactional
    public void deleteByToken(String token) {
        refreshTokenJpaRepository.deleteByToken(token);
    }

    @Override
    @Transactional
    public void deleteByUserId(Long userId) {
        refreshTokenJpaRepository.deleteByUserId(userId);
    }
}
