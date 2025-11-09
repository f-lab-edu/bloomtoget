package com.btg.core.application.service.auth;

import com.btg.core.application.port.in.auth.RefreshTokenUseCase;
import com.btg.core.application.port.out.auth.GenerateTokenPort;
import com.btg.core.application.port.out.auth.LoadRefreshTokenPort;
import com.btg.core.application.port.out.user.LoadUserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RefreshTokenService implements RefreshTokenUseCase {

    private final LoadRefreshTokenPort loadRefreshTokenPort;
    private final LoadUserPort loadUserPort;
    private final GenerateTokenPort generateTokenPort;

    @Override
    public TokenResult refreshToken(RefreshTokenCommand command) {
        if (!generateTokenPort.validateToken(command.refreshToken())) {
            throw new IllegalArgumentException("refresh token이 유효하지 않습니다");
        }

        LoadRefreshTokenPort.RefreshToken refreshToken = loadRefreshTokenPort
                .loadByToken(command.refreshToken())
                .orElseThrow(() -> new IllegalArgumentException("Refresh token을 찾을 수 없습니다"));

        if (refreshToken.expiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Refresh token이 만료되었습니다");
        }

        LoadUserPort.User user = loadUserPort.loadById(refreshToken.userId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        String newAccessToken = generateTokenPort.generateAccessToken(user.id(), user.email());

        return new TokenResult(newAccessToken);
    }
}
