package com.btg.core.application.port.out.auth;

import java.time.LocalDateTime;

public interface SaveRefreshTokenPort {

    void save(String token, Long userId, LocalDateTime expiresAt);
}
