package com.btg.core.application.port.out.auth;

import java.time.LocalDateTime;
import java.util.Optional;

public interface LoadRefreshTokenPort {

    Optional<RefreshToken> loadByToken(String token);

    record RefreshToken(
            Long id,
            String token,
            Long userId,
            LocalDateTime expiresAt,
            LocalDateTime createdAt
    ) {}
}
