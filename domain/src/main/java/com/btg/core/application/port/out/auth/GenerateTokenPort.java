package com.btg.core.application.port.out.auth;

import java.time.LocalDateTime;

public interface GenerateTokenPort {

    String generateAccessToken(Long userId, String email);

    String generateRefreshToken(Long userId);

    boolean validateToken(String token);

    Long getUserIdFromToken(String token);

    String getEmailFromToken(String token);

    LocalDateTime getExpirationFromToken(String token);
}
