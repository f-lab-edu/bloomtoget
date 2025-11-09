package com.btg.core.application.service.auth;

import com.btg.core.application.port.in.auth.SignupUseCase;
import com.btg.core.application.port.out.auth.EncodePasswordPort;
import com.btg.core.application.port.out.user.LoadUserPort;
import com.btg.core.application.port.out.user.SaveUserPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;


@ExtendWith(MockitoExtension.class)
@DisplayName("SignupService 단위 테스트")
class SignupServiceTest {

    @Mock
    private LoadUserPort loadUserPort;

    @Mock
    private SaveUserPort saveUserPort;

    @Mock
    private EncodePasswordPort encodePasswordPort;

    @InjectMocks
    private SignupService signupService;

    @Test
    @DisplayName("정상적인 회원가입 - 성공")
    void signup_Success() {
        // Given
        SignupUseCase.SignupCommand command = new SignupUseCase.SignupCommand(
                "test@example.com",
                "password123",
                "Test User"
        );

        given(loadUserPort.existsByEmail("test@example.com")).willReturn(false);
        given(encodePasswordPort.encode("password123")).willReturn("$2a$10$encodedPassword");

        SaveUserPort.User savedUser = new SaveUserPort.User(
                1L,
                "test@example.com",
                "Test User",
                "2025-11-05T10:00:00"
        );
        given(saveUserPort.save("test@example.com", "$2a$10$encodedPassword", "Test User"))
                .willReturn(savedUser);

        // When
        SignupUseCase.UserResult result = signupService.signup(command);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.email()).isEqualTo("test@example.com");
        assertThat(result.name()).isEqualTo("Test User");
        assertThat(result.createdAt()).isEqualTo("2025-11-05T10:00:00");

        then(loadUserPort).should().existsByEmail("test@example.com");
        then(encodePasswordPort).should().encode("password123");
        then(saveUserPort).should().save("test@example.com", "$2a$10$encodedPassword", "Test User");
    }

    @Test
    @DisplayName("중복 이메일로 회원가입 시도 - 실패")
    void signup_DuplicateEmail_ThrowsException() {
        // Given
        SignupUseCase.SignupCommand command = new SignupUseCase.SignupCommand(
                "duplicate@example.com",
                "password123",
                "Test User"
        );

        given(loadUserPort.existsByEmail("duplicate@example.com")).willReturn(true);

        // When & Then
        assertThatThrownBy(() -> signupService.signup(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 존재하는 이메일입니다: duplicate@example.com");

        then(loadUserPort).should().existsByEmail("duplicate@example.com");
        then(encodePasswordPort).should(never()).encode(anyString());
        then(saveUserPort).should(never()).save(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("비밀번호 암호화 검증 - 성공")
    void signup_PasswordEncoding_Success() {
        // Given
        String rawPassword = "mySecurePassword";
        String encodedPassword = "$2a$10$encrypted.hash.value";

        SignupUseCase.SignupCommand command = new SignupUseCase.SignupCommand(
                "security@example.com",
                rawPassword,
                "Security User"
        );

        given(loadUserPort.existsByEmail(anyString())).willReturn(false);
        given(encodePasswordPort.encode(rawPassword)).willReturn(encodedPassword);

        SaveUserPort.User savedUser = new SaveUserPort.User(
                2L,
                "security@example.com",
                "Security User",
                "2025-11-05T11:00:00"
        );
        given(saveUserPort.save(anyString(), anyString(), anyString())).willReturn(savedUser);

        // When
        signupService.signup(command);

        // Then
        then(encodePasswordPort).should().encode(rawPassword);
        then(saveUserPort).should().save(
                "security@example.com",
                encodedPassword,  // 암호화된 비밀번호가 전달되었는지 검증
                "Security User"
        );
    }

    @Test
    @DisplayName("모든 필드가 올바르게 전달되는지 검증 - 성공")
    void signup_AllFieldsPassedCorrectly_Success() {
        // Given
        SignupUseCase.SignupCommand command = new SignupUseCase.SignupCommand(
                "complete@example.com",
                "password999",
                "Complete User"
        );

        given(loadUserPort.existsByEmail("complete@example.com")).willReturn(false);
        given(encodePasswordPort.encode("password999")).willReturn("encoded999");

        SaveUserPort.User savedUser = new SaveUserPort.User(
                3L,
                "complete@example.com",
                "Complete User",
                "2025-11-05T12:00:00"
        );
        given(saveUserPort.save("complete@example.com", "encoded999", "Complete User"))
                .willReturn(savedUser);

        // When
        SignupUseCase.UserResult result = signupService.signup(command);

        // Then
        assertThat(result.email()).isEqualTo(command.email());
        assertThat(result.name()).isEqualTo(command.name());

        then(saveUserPort).should().save(
                command.email(),
                "encoded999",
                command.name()
        );
    }

    @Test
    @DisplayName("이메일 중복 체크가 가장 먼저 수행되는지 검증 - 성공")
    void signup_EmailCheckFirst_Success() {
        // Given
        SignupUseCase.SignupCommand command = new SignupUseCase.SignupCommand(
                "duplicate@example.com",
                "password123",
                "Test User"
        );

        given(loadUserPort.existsByEmail("duplicate@example.com")).willReturn(true);

        // When & Then
        assertThatThrownBy(() -> signupService.signup(command))
                .isInstanceOf(IllegalArgumentException.class);

        // 이메일 중복 체크 후 바로 실패했으므로, 암호화 및 저장은 호출되지 않아야 함
        then(loadUserPort).should().existsByEmail("duplicate@example.com");
        then(encodePasswordPort).should(never()).encode(anyString());
        then(saveUserPort).should(never()).save(anyString(), anyString(), anyString());
    }
}
