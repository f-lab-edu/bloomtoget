package com.btg.core.application.port.in.user;

public interface GetUserProfileUseCase {
    UserProfileResult getUserProfile(Long userId);

    record UserProfileResult(
        Long id,
        String email,
        String name,
        Long createdAt
    ) {}
}
