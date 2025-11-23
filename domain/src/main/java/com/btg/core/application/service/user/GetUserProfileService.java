package com.btg.core.application.service.user;

import com.btg.core.application.port.in.user.GetUserProfileUseCase;
import com.btg.core.application.port.out.user.LoadUserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetUserProfileService implements GetUserProfileUseCase {

    private final LoadUserPort loadUserPort;

    @Override
    public UserProfileResult getUserProfile(Long userId) {
        LoadUserPort.User user = loadUserPort.loadById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        return new UserProfileResult(
                user.id(),
                user.email(),
                user.name(),
                user.createdAt()
        );
    }
}
