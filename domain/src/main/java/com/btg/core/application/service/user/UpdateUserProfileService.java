package com.btg.core.application.service.user;

import com.btg.core.application.port.in.user.UpdateUserProfileUseCase;
import com.btg.core.application.port.out.auth.EncodePasswordPort;
import com.btg.core.application.port.out.user.UpdateUserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateUserProfileService implements UpdateUserProfileUseCase {

    private final UpdateUserPort updateUserPort;
    private final EncodePasswordPort encodePasswordPort;

    @Override
    public UserProfileResult updateUserProfile(UpdateUserProfileCommand command) {

        String encodedPassword = null;
        if (command.password() != null && !command.password().isBlank()) {
            encodedPassword = encodePasswordPort.encode(command.password());
        }

        UpdateUserPort.User updatedUser = updateUserPort.update(
                command.userId(),
                command.name(),
                encodedPassword
        );

        return new UserProfileResult(
                updatedUser.id(),
                updatedUser.email(),
                updatedUser.name(),
                updatedUser.createdAt()
        );
    }
}
