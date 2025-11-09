package com.btg.core.application.service.auth;

import com.btg.core.application.port.in.auth.SignupUseCase;
import com.btg.core.application.port.out.auth.EncodePasswordPort;
import com.btg.core.application.port.out.user.LoadUserPort;
import com.btg.core.application.port.out.user.SaveUserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SignupService implements SignupUseCase {

    private final LoadUserPort loadUserPort;
    private final SaveUserPort saveUserPort;
    private final EncodePasswordPort encodePasswordPort;

    @Override
    public UserResult signup(SignupCommand command) {
        if (loadUserPort.existsByEmail(command.email())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다: " + command.email());
        }

        String encodedPassword = encodePasswordPort.encode(command.password());

        SaveUserPort.User savedUser = saveUserPort.save(
                command.email(),
                encodedPassword,
                command.name()
        );

        return new UserResult(
                savedUser.id(),
                savedUser.email(),
                savedUser.name(),
                savedUser.createdAt()
        );
    }
}
