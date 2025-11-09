package com.btg.infrastructure.security.jwt;

import com.btg.core.application.port.out.auth.GenerateTokenPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class JwtTokenAdapter implements GenerateTokenPort {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public String generateAccessToken(Long userId, String email) {
        return jwtTokenProvider.generateAccessToken(userId, email);
    }

    @Override
    public String generateRefreshToken(Long userId) {
        return jwtTokenProvider.generateRefreshToken(userId);
    }

    @Override
    public boolean validateToken(String token) {
        return jwtTokenProvider.validateToken(token);
    }

    @Override
    public Long getUserIdFromToken(String token) {
        return jwtTokenProvider.getUserIdFromToken(token);
    }

    @Override
    public String getEmailFromToken(String token) {
        return jwtTokenProvider.getEmailFromToken(token);
    }

    @Override
    public LocalDateTime getExpirationFromToken(String token) {
        return jwtTokenProvider.getExpirationFromToken(token);
    }
}
