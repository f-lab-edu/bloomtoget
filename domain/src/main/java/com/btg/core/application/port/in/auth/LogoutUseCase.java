package com.btg.core.application.port.in.auth;

public interface LogoutUseCase {
    void logout(LogoutCommand command);

    record LogoutCommand(
        String refreshToken
    ) {
        public LogoutCommand {
            if (refreshToken == null || refreshToken.isBlank()) {
                throw new IllegalArgumentException("Refresh token is required");
            }
        }
    }
}
