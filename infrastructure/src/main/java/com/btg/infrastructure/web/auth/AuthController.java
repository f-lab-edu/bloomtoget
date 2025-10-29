package com.btg.infrastructure.web.auth;

import com.btg.core.application.port.in.auth.*;
import com.btg.infrastructure.web.auth.dto.request.LoginRequest;
import com.btg.infrastructure.web.auth.dto.request.RefreshTokenRequest;
import com.btg.infrastructure.web.auth.dto.request.SignupRequest;
import com.btg.infrastructure.web.auth.dto.response.LoginResponse;
import com.btg.infrastructure.web.auth.dto.response.TokenResponse;
import com.btg.infrastructure.web.user.dto.response.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SignupUseCase signupUseCase;
    private final LoginUseCase loginUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final LogoutUseCase logoutUseCase;

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(@Valid @RequestBody SignupRequest request) {
        SignupUseCase.SignupCommand command = new SignupUseCase.SignupCommand(
            request.email(),
            request.password(),
            request.name()
        );

        SignupUseCase.UserResult result = signupUseCase.signup(command);

        UserResponse response = new UserResponse(
            result.id(),
            result.email(),
            result.name(),
            result.createdAt()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginUseCase.LoginCommand command = new LoginUseCase.LoginCommand(
            request.email(),
            request.password()
        );

        LoginUseCase.LoginResult result = loginUseCase.login(command);

        UserResponse userResponse = new UserResponse(
            result.user().id(),
            result.user().email(),
            result.user().name(),
            result.user().createdAt()
        );

        LoginResponse response = new LoginResponse(
            result.accessToken(),
            result.refreshToken(),
            userResponse
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        RefreshTokenUseCase.RefreshTokenCommand command = new RefreshTokenUseCase.RefreshTokenCommand(request.refreshToken());
        RefreshTokenUseCase.TokenResult result = refreshTokenUseCase.refreshToken(command);

        TokenResponse response = new TokenResponse(result.accessToken());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
        LogoutUseCase.LogoutCommand command = new LogoutUseCase.LogoutCommand(request.refreshToken());
        logoutUseCase.logout(command);

        return ResponseEntity.noContent().build();
    }
}
