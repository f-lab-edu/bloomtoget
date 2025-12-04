package com.btg.core.application.port.in.auth;

public interface SignupUseCase {
    UserResult signup(SignupCommand command);

    record SignupCommand(
        String email,
        String password,
        String name
    ) {
        public SignupCommand {
            // Self-validation
            if (email == null || email.isBlank()) {
                throw new IllegalArgumentException("Email is required");
            }
            if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$")) {
                throw new IllegalArgumentException("Invalid email format");
            }
            if (password == null || password.isBlank()) {
                throw new IllegalArgumentException("Password is required");
            }
            if (password.length() < 8 || password.length() > 100) {
                throw new IllegalArgumentException("Password must be between 8 and 100 characters");
            }
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("Name is required");
            }
            if (name.length() < 2 || name.length() > 50) {
                throw new IllegalArgumentException("Name must be between 2 and 50 characters");
            }
        }
    }

    record UserResult(
        Long id,
        String email,
        String name,
        Long createdAt
    ) {}
}
