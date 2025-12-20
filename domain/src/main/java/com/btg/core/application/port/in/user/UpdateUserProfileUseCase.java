package com.btg.core.application.port.in.user;

public interface UpdateUserProfileUseCase {
    UserProfileResult updateUserProfile(UpdateUserProfileCommand command);

    record UpdateUserProfileCommand(
        Long userId,
        String name,
        String password
    ) {
        public UpdateUserProfileCommand {
            if (userId == null || userId <= 0) {
                throw new IllegalArgumentException("Valid user ID is required");
            }
            // name과 password는 optional (null 허용)
            if (name != null && !name.isBlank()) {
                if (name.length() < 2 || name.length() > 50) {
                    throw new IllegalArgumentException("Name must be between 2 and 50 characters");
                }
            }
            if (password != null && !password.isBlank()) {
                if (password.length() < 8 || password.length() > 100) {
                    throw new IllegalArgumentException("Password must be between 8 and 100 characters");
                }
            }
        }
    }

    record UserProfileResult(
        Long id,
        String email,
        String name,
        Long createdAt
    ) {}
}
