package com.btg.core.application.port.in.auth;

public interface RefreshTokenUseCase {
    TokenResult refreshToken(RefreshTokenCommand command);

    record RefreshTokenCommand(
        String refreshToken
    ) {
        public RefreshTokenCommand {
            if (refreshToken == null || refreshToken.isBlank()) {
                throw new IllegalArgumentException("Refresh token is required");
            }
        }
    }

    record TokenResult(
        String accessToken
    ) {}
}
