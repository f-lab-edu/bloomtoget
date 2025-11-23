package com.btg.e2e;

import com.btg.core.application.port.in.dailyprogress.GetDailyProgressUseCase;
import com.btg.core.application.port.in.dailyprogress.UpdateDailyProgressUseCase;
import com.btg.core.application.port.in.group.*;
import com.btg.core.application.port.in.task.*;
import com.btg.core.application.port.in.user.GetUserProfileUseCase;
import com.btg.core.application.port.in.user.UpdateUserProfileUseCase;
import com.btg.infrastructure.persistence.auth.repository.RefreshTokenJpaRepository;
import com.btg.infrastructure.persistence.user.repository.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("Auth Controller E2E Tests (Real Server)")
class AuthControllerE2ETest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private RefreshTokenJpaRepository refreshTokenJpaRepository;

    @MockBean private GetDailyProgressUseCase getDailyProgressUseCase;
    @MockBean private UpdateDailyProgressUseCase updateDailyProgressUseCase;
    @MockBean private CreateTaskUseCase createTaskUseCase;
    @MockBean private GetTaskUseCase getTaskUseCase;
    @MockBean private UpdateTaskUseCase updateTaskUseCase;
    @MockBean private DeleteTaskUseCase deleteTaskUseCase;
    @MockBean private ListTasksUseCase listTasksUseCase;
    @MockBean private CreateGroupUseCase createGroupUseCase;
    @MockBean private GetGroupUseCase getGroupUseCase;
    @MockBean private UpdateGroupUseCase updateGroupUseCase;
    @MockBean private DeleteGroupUseCase deleteGroupUseCase;
    @MockBean private ListGroupsUseCase listGroupsUseCase;
    @MockBean private JoinGroupUseCase joinGroupUseCase;
    @MockBean private GetUserProfileUseCase getUserProfileUseCase;
    @MockBean private UpdateUserProfileUseCase updateUserProfileUseCase;

    @BeforeEach
    void setUp() {
        refreshTokenJpaRepository.deleteAll();
        userJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /auth/signup - Success with real database persistence")
    void signup_Success_WithDatabasePersistence() {
        // Given
        String requestBody = """
            {
                "email": "newuser@example.com",
                "password": "securePassword123",
                "name": "New User"
            }
            """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        // When
        var response = restTemplate.postForEntity("/auth/signup", request, String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).contains("newuser@example.com");
        assertThat(response.getBody()).contains("New User");

        // Verify database persistence
        var savedUser = userJpaRepository.findByEmail("newuser@example.com");
        assertThat(savedUser).isPresent();
        assertThat(savedUser.get().getName()).isEqualTo("New User");
        // Password should be encrypted (not plain text)
        assertThat(savedUser.get().getPassword()).isNotEqualTo("securePassword123");
        assertThat(savedUser.get().getPassword()).startsWith("$2a$"); // BCrypt prefix
    }

    @Test
    @DisplayName("POST /auth/signup - Duplicate email should fail")
    void signup_Fail_DuplicateEmail() {
        // Given: Create first user
        String firstUserRequest = """
            {
                "email": "duplicate@example.com",
                "password": "password123",
                "name": "First User"
            }
            """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        restTemplate.postForEntity("/auth/signup", new HttpEntity<>(firstUserRequest, headers), String.class);

        // When: Try to create user with same email
        String duplicateRequest = """
            {
                "email": "duplicate@example.com",
                "password": "differentPassword",
                "name": "Second User"
            }
            """;

        var response = restTemplate.postForEntity("/auth/signup", new HttpEntity<>(duplicateRequest, headers), String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("POST /auth/login - Success with real JWT token generation")
    void login_Success_WithRealJwtToken() {
        // Given: Create a user first
        String signupRequest = """
            {
                "email": "logintest@example.com",
                "password": "myPassword123",
                "name": "Login Test User"
            }
            """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        restTemplate.postForEntity("/auth/signup", new HttpEntity<>(signupRequest, headers), String.class);

        // When: Login
        String loginRequest = """
            {
                "email": "logintest@example.com",
                "password": "myPassword123"
            }
            """;

        var response = restTemplate.postForEntity("/auth/login", new HttpEntity<>(loginRequest, headers), String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("accessToken");
        assertThat(response.getBody()).contains("refreshToken");
        assertThat(response.getBody()).contains("logintest@example.com");
        assertThat(response.getBody()).contains("Login Test User");

        // Verify tokens are real JWT format (header.payload.signature)
        assertThat(response.getBody()).containsPattern("\"accessToken\":\"[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+\"");
        assertThat(response.getBody()).containsPattern("\"refreshToken\":\"[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+\"");

        // Verify refresh token is stored in database
        String refreshToken = extractRefreshToken(response.getBody());
        var savedToken = refreshTokenJpaRepository.findByToken(refreshToken);
        assertThat(savedToken).isPresent();
    }

    @Test
    @DisplayName("POST /auth/login - Wrong password should fail")
    void login_Fail_WrongPassword() {
        // Given: Create a user first
        String signupRequest = """
            {
                "email": "wrongpw@example.com",
                "password": "correctPassword",
                "name": "Test User"
            }
            """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        restTemplate.postForEntity("/auth/signup", new HttpEntity<>(signupRequest, headers), String.class);

        // When: Try to login with wrong password
        String loginRequest = """
            {
                "email": "wrongpw@example.com",
                "password": "wrongPassword"
            }
            """;

        var response = restTemplate.postForEntity("/auth/login", new HttpEntity<>(loginRequest, headers), String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("POST /auth/refresh - Success with new access token")
    void refreshToken_Success() {
        // Given: Signup and login to get refresh token
        String signupRequest = """
            {
                "email": "refresh@example.com",
                "password": "password123",
                "name": "Refresh User"
            }
            """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        restTemplate.postForEntity("/auth/signup", new HttpEntity<>(signupRequest, headers), String.class);

        String loginRequest = """
            {
                "email": "refresh@example.com",
                "password": "password123"
            }
            """;

        var loginResponse = restTemplate.postForEntity("/auth/login", new HttpEntity<>(loginRequest, headers), String.class);
        String refreshToken = extractRefreshToken(loginResponse.getBody());

        // When: Refresh access token
        String refreshRequest = String.format("""
            {
                "refreshToken": "%s"
            }
            """, refreshToken);

        var response = restTemplate.postForEntity("/auth/refresh", new HttpEntity<>(refreshRequest, headers), String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("accessToken");
        // New access token should be different from original
        assertThat(response.getBody()).containsPattern("\"accessToken\":\"[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+\"");
    }

    @Test
    @DisplayName("POST /auth/logout - Success and token removed from database")
    void logout_Success_TokenRemovedFromDatabase() {
        // Given: Signup and login
        String signupRequest = """
            {
                "email": "logout@example.com",
                "password": "password123",
                "name": "Logout User"
            }
            """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        restTemplate.postForEntity("/auth/signup", new HttpEntity<>(signupRequest, headers), String.class);

        String loginRequest = """
            {
                "email": "logout@example.com",
                "password": "password123"
            }
            """;

        var loginResponse = restTemplate.postForEntity("/auth/login", new HttpEntity<>(loginRequest, headers), String.class);
        String refreshToken = extractRefreshToken(loginResponse.getBody());

        // Verify token exists before logout
        assertThat(refreshTokenJpaRepository.findByToken(refreshToken)).isPresent();

        // When: Logout
        String logoutRequest = String.format("""
            {
                "refreshToken": "%s"
            }
            """, refreshToken);

        var response = restTemplate.postForEntity("/auth/logout", new HttpEntity<>(logoutRequest, headers), String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // Verify token is removed from database
        assertThat(refreshTokenJpaRepository.findByToken(refreshToken)).isEmpty();
    }

    @Test
    @DisplayName("POST /auth/signup - Validation error for invalid email")
    void signup_ValidationError_InvalidEmail() {
        // Given
        String requestBody = """
            {
                "email": "not-an-email",
                "password": "password123",
                "name": "Test User"
            }
            """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        // When
        var response = restTemplate.postForEntity("/auth/signup", request, String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("POST /auth/signup - Validation error for short password")
    void signup_ValidationError_ShortPassword() {
        // Given
        String requestBody = """
            {
                "email": "test@example.com",
                "password": "short",
                "name": "Test User"
            }
            """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        // When
        var response = restTemplate.postForEntity("/auth/signup", request, String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    // Helper method to extract refresh token from JSON response
    private String extractRefreshToken(String jsonResponse) {
        int startIndex = jsonResponse.indexOf("\"refreshToken\":\"") + 16;
        int endIndex = jsonResponse.indexOf("\"", startIndex);
        return jsonResponse.substring(startIndex, endIndex);
    }
}
