package com.btg.core.application.service.auth;

import com.btg.core.application.port.in.auth.LogoutUseCase;
import com.btg.core.application.port.out.auth.DeleteRefreshTokenPort;
import com.btg.core.application.port.out.auth.GenerateTokenPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LogoutService implements LogoutUseCase {

    private final DeleteRefreshTokenPort deleteRefreshTokenPort;
    private final GenerateTokenPort generateTokenPort;

    @Override
    public void logout(LogoutCommand command) {
        if (!generateTokenPort.validateToken(command.refreshToken())) {
            throw new IllegalArgumentException("refresh token이 유효하지 않습니다");
        }

        deleteRefreshTokenPort.deleteByToken(command.refreshToken());
    }
}
