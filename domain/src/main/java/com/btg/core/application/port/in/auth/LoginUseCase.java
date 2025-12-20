package com.btg.core.application.port.in.auth;

public interface LoginUseCase {
    LoginResult login(LoginCommand command);

    record LoginCommand(
        String email,
        String password
    ) {
        public LoginCommand {
            if (email == null || email.isBlank()) {
                throw new IllegalArgumentException("Email is required");
            }
            if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$")) {
                throw new IllegalArgumentException("Invalid email format");
            }
            if (password == null || password.isBlank()) {
                throw new IllegalArgumentException("Password is required");
            }
        }
    }

    record LoginResult(
        String accessToken,
        String refreshToken,
        UserInfo user
    ) {
        public record UserInfo(
            Long id,
            String email,
            String name,
            Long createdAt
        ) {}
    }
}
