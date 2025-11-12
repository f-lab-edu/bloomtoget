package com.btg.core.application.service.auth;

import com.btg.core.application.port.in.auth.LoginUseCase;
import com.btg.core.application.port.out.auth.DeleteRefreshTokenPort;
import com.btg.core.application.port.out.auth.EncodePasswordPort;
import com.btg.core.application.port.out.auth.GenerateTokenPort;
import com.btg.core.application.port.out.auth.SaveRefreshTokenPort;
import com.btg.core.application.port.out.user.LoadUserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LoginService implements LoginUseCase {

    private final LoadUserPort loadUserPort;
    private final SaveRefreshTokenPort saveRefreshTokenPort;
    private final DeleteRefreshTokenPort deleteRefreshTokenPort;
    private final EncodePasswordPort encodePasswordPort;
    private final GenerateTokenPort generateTokenPort;

    @Override
    public LoginResult login(LoginCommand command) {
        LoadUserPort.User user = loadUserPort.loadByEmail(command.email())
                .orElseThrow(() -> new IllegalArgumentException("이메일이나 패스워드를 확인해 주세요"));

        if (!encodePasswordPort.matches(command.password(), user.password())) {
            throw new IllegalArgumentException("이메일이나 패스워드를 확인해 주세요");
        }

        String accessToken = generateTokenPort.generateAccessToken(user.id(), user.email());
        String refreshToken = generateTokenPort.generateRefreshToken(user.id());

        deleteRefreshTokenPort.deleteByUserId(user.id());

        saveRefreshTokenPort.save(
                refreshToken,
                user.id(),
                generateTokenPort.getExpirationFromToken(refreshToken)
        );

        return new LoginResult(
                accessToken,
                refreshToken,
                new LoginResult.UserInfo(
                        user.id(),
                        user.email(),
                        user.name(),
                        user.createdAt()
                )
        );
    }
}
