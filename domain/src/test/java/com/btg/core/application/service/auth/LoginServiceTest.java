package com.btg.core.application.service.auth;

import com.btg.core.application.port.in.auth.LoginUseCase;
import com.btg.core.application.port.out.auth.DeleteRefreshTokenPort;
import com.btg.core.application.port.out.auth.EncodePasswordPort;
import com.btg.core.application.port.out.auth.GenerateTokenPort;
import com.btg.core.application.port.out.auth.SaveRefreshTokenPort;
import com.btg.core.application.port.out.user.LoadUserPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoginService 단위 테스트")
class LoginServiceTest {

    @Mock
    private LoadUserPort loadUserPort;

    @Mock
    private SaveRefreshTokenPort saveRefreshTokenPort;

    @Mock
    private DeleteRefreshTokenPort deleteRefreshTokenPort;

    @Mock
    private EncodePasswordPort encodePasswordPort;

    @Mock
    private GenerateTokenPort generateTokenPort;

    @InjectMocks
    private LoginService loginService;

    @Test
    @DisplayName("정상 로그인 - 성공")
    void login_Success() {
        // Given
        LoginUseCase.LoginCommand command = new LoginUseCase.LoginCommand(
                "test@example.com",
                "password123"
        );

        LoadUserPort.User user = new LoadUserPort.User(
                1L,
                "test@example.com",
                "$2a$10$encodedPassword",
                "Test User",
                "2025-11-05T10:00:00"
        );
        given(loadUserPort.loadByEmail("test@example.com")).willReturn(Optional.of(user));
        given(encodePasswordPort.matches("password123", "$2a$10$encodedPassword")).willReturn(true);

        String accessToken = "eyJhbGciOiJIUzI1NiJ9.accessToken";
        String refreshToken = "eyJhbGciOiJIUzI1NiJ9.refreshToken";
        given(generateTokenPort.generateAccessToken(1L, "test@example.com")).willReturn(accessToken);
        given(generateTokenPort.generateRefreshToken(1L)).willReturn(refreshToken);
        given(generateTokenPort.getExpirationFromToken(refreshToken))
                .willReturn(LocalDateTime.now().plusDays(7));

        // When
        LoginUseCase.LoginResult result = loginService.login(command);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.accessToken()).isEqualTo(accessToken);
        assertThat(result.refreshToken()).isEqualTo(refreshToken);
        assertThat(result.user()).isNotNull();
        assertThat(result.user().id()).isEqualTo(1L);
        assertThat(result.user().email()).isEqualTo("test@example.com");
        assertThat(result.user().name()).isEqualTo("Test User");

        then(loadUserPort).should().loadByEmail("test@example.com");
        then(encodePasswordPort).should().matches("password123", "$2a$10$encodedPassword");
        then(generateTokenPort).should().generateAccessToken(1L, "test@example.com");
        then(generateTokenPort).should().generateRefreshToken(1L);
        then(deleteRefreshTokenPort).should().deleteByUserId(1L);
        then(saveRefreshTokenPort).should().save(anyString(), anyLong(), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 로그인 시도 - 실패")
    void login_UserNotFound_ThrowsException() {
        // Given
        LoginUseCase.LoginCommand command = new LoginUseCase.LoginCommand(
                "nonexistent@example.com",
                "password123"
        );

        given(loadUserPort.loadByEmail("nonexistent@example.com")).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> loginService.login(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이메일이나 패스워드를 확인해 주세요");

        then(loadUserPort).should().loadByEmail("nonexistent@example.com");
        then(encodePasswordPort).should(never()).matches(anyString(), anyString());
        then(generateTokenPort).should(never()).generateAccessToken(anyLong(), anyString());
        then(deleteRefreshTokenPort).should(never()).deleteByUserId(anyLong());
    }

    @Test
    @DisplayName("잘못된 비밀번호로 로그인 시도 - 실패")
    void login_WrongPassword_ThrowsException() {
        // Given
        LoginUseCase.LoginCommand command = new LoginUseCase.LoginCommand(
                "test@example.com",
                "wrongPassword"
        );

        LoadUserPort.User user = new LoadUserPort.User(
                1L,
                "test@example.com",
                "$2a$10$encodedPassword",
                "Test User",
                "2025-11-05T10:00:00"
        );
        given(loadUserPort.loadByEmail("test@example.com")).willReturn(Optional.of(user));
        given(encodePasswordPort.matches("wrongPassword", "$2a$10$encodedPassword")).willReturn(false);

        // When & Then
        assertThatThrownBy(() -> loginService.login(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이메일이나 패스워드를 확인해 주세요");

        then(loadUserPort).should().loadByEmail("test@example.com");
        then(encodePasswordPort).should().matches("wrongPassword", "$2a$10$encodedPassword");
        then(generateTokenPort).should(never()).generateAccessToken(anyLong(), anyString());
        then(deleteRefreshTokenPort).should(never()).deleteByUserId(anyLong());
    }

    @Test
    @DisplayName("로그인 시 이전 Refresh Token 삭제 검증 - 성공")
    void login_DeleteOldRefreshToken_Success() {
        // Given
        LoginUseCase.LoginCommand command = new LoginUseCase.LoginCommand(
                "test@example.com",
                "password123"
        );

        LoadUserPort.User user = new LoadUserPort.User(
                1L,
                "test@example.com",
                "$2a$10$encodedPassword",
                "Test User",
                "2025-11-05T10:00:00"
        );
        given(loadUserPort.loadByEmail(anyString())).willReturn(Optional.of(user));
        given(encodePasswordPort.matches(anyString(), anyString())).willReturn(true);

        String refreshToken = "eyJhbGciOiJIUzI1NiJ9.refreshToken";
        given(generateTokenPort.generateAccessToken(anyLong(), anyString())).willReturn("accessToken");
        given(generateTokenPort.generateRefreshToken(anyLong())).willReturn(refreshToken);
        given(generateTokenPort.getExpirationFromToken(refreshToken))
                .willReturn(LocalDateTime.now().plusDays(7));

        // When
        loginService.login(command);

        // Then
        then(deleteRefreshTokenPort).should().deleteByUserId(1L);
        then(saveRefreshTokenPort).should().save(anyString(), anyLong(), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("로그인 시 새 Refresh Token 저장 검증 - 성공")
    void login_SaveNewRefreshToken_Success() {
        // Given
        LoginUseCase.LoginCommand command = new LoginUseCase.LoginCommand(
                "test@example.com",
                "password123"
        );

        LoadUserPort.User user = new LoadUserPort.User(
                1L,
                "test@example.com",
                "$2a$10$encodedPassword",
                "Test User",
                "2025-11-05T10:00:00"
        );
        given(loadUserPort.loadByEmail(anyString())).willReturn(Optional.of(user));
        given(encodePasswordPort.matches(anyString(), anyString())).willReturn(true);

        String accessToken = "eyJhbGciOiJIUzI1NiJ9.accessToken";
        String refreshToken = "eyJhbGciOiJIUzI1NiJ9.refreshToken";
        LocalDateTime expiration = LocalDateTime.of(2025, 11, 16, 10, 0);

        given(generateTokenPort.generateAccessToken(1L, "test@example.com")).willReturn(accessToken);
        given(generateTokenPort.generateRefreshToken(1L)).willReturn(refreshToken);
        given(generateTokenPort.getExpirationFromToken(refreshToken)).willReturn(expiration);

        // When
        loginService.login(command);

        // Then
        then(saveRefreshTokenPort).should().save(refreshToken, 1L, expiration);
    }

    @Test
    @DisplayName("로그인 후 모든 정보가 올바르게 반환되는지 검증 - 성공")
    void login_ReturnsCompleteUserInfo_Success() {
        // Given
        LoginUseCase.LoginCommand command = new LoginUseCase.LoginCommand(
                "complete@example.com",
                "password999"
        );

        LoadUserPort.User user = new LoadUserPort.User(
                99L,
                "complete@example.com",
                "$2a$10$hash",
                "Complete User",
                "2025-11-05T15:30:00"
        );
        given(loadUserPort.loadByEmail("complete@example.com")).willReturn(Optional.of(user));
        given(encodePasswordPort.matches("password999", "$2a$10$hash")).willReturn(true);

        String accessToken = "access.token.value";
        String refreshToken = "refresh.token.value";
        given(generateTokenPort.generateAccessToken(99L, "complete@example.com")).willReturn(accessToken);
        given(generateTokenPort.generateRefreshToken(99L)).willReturn(refreshToken);
        given(generateTokenPort.getExpirationFromToken(refreshToken))
                .willReturn(LocalDateTime.now().plusDays(7));

        // When
        LoginUseCase.LoginResult result = loginService.login(command);

        // Then
        assertThat(result.accessToken()).isEqualTo(accessToken);
        assertThat(result.refreshToken()).isEqualTo(refreshToken);
        assertThat(result.user().id()).isEqualTo(99L);
        assertThat(result.user().email()).isEqualTo("complete@example.com");
        assertThat(result.user().name()).isEqualTo("Complete User");
        assertThat(result.user().createdAt()).isEqualTo("2025-11-05T15:30:00");
    }

    @Test
    @DisplayName("로그인 프로세스 실행 순서 검증 - 성공")
    void login_ExecutionOrder_Success() {
        // Given
        LoginUseCase.LoginCommand command = new LoginUseCase.LoginCommand(
                "order@example.com",
                "password123"
        );

        LoadUserPort.User user = new LoadUserPort.User(
                1L,
                "order@example.com",
                "$2a$10$hash",
                "Order User",
                "2025-11-05T10:00:00"
        );
        given(loadUserPort.loadByEmail(anyString())).willReturn(Optional.of(user));
        given(encodePasswordPort.matches(anyString(), anyString())).willReturn(true);
        given(generateTokenPort.generateAccessToken(anyLong(), anyString())).willReturn("accessToken");
        given(generateTokenPort.generateRefreshToken(anyLong())).willReturn("refreshToken");
        given(generateTokenPort.getExpirationFromToken(anyString()))
                .willReturn(LocalDateTime.now().plusDays(7));

        // When
        loginService.login(command);

        // Then - 모든 Port가 정확히 1번씩 호출되었는지 검증
        then(loadUserPort).should().loadByEmail("order@example.com");
        then(encodePasswordPort).should().matches("password123", "$2a$10$hash");
        then(generateTokenPort).should().generateAccessToken(1L, "order@example.com");
        then(generateTokenPort).should().generateRefreshToken(1L);
        then(deleteRefreshTokenPort).should().deleteByUserId(1L);
        then(saveRefreshTokenPort).should().save(anyString(), anyLong(), any(LocalDateTime.class));
    }
}
