package com.btg.e2e;

import com.btg.core.application.port.in.dailyprogress.GetDailyProgressUseCase;
import com.btg.core.application.port.in.dailyprogress.UpdateDailyProgressUseCase;
import com.btg.core.application.port.in.group.*;
import com.btg.core.application.port.in.task.*;
import com.btg.infrastructure.persistence.user.entity.UserJpaEntity;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("User Controller E2E Tests (Real Server)")
class UserControllerE2ETest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JdbcTemplate jdbcTemplate;

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

    private UserJpaEntity testUser;

    @BeforeEach
    void setUp() {
        // 1. 기존 데이터 정리
        userJpaRepository.deleteAll();

        // 2. H2 AUTO_INCREMENT 초기화 (ID를 1부터 시작)
        jdbcTemplate.execute("ALTER TABLE users ALTER COLUMN id RESTART WITH 1");

        // 3. 테스트 사용자 생성 (이제 ID=1로 저장됨)
        testUser = new UserJpaEntity(
                "testuser@example.com",
                passwordEncoder.encode("password123"),
                "Test User"
        );
        testUser = userJpaRepository.save(testUser);

        // 4. 디버깅: 실제 저장된 ID 확인
        System.out.println("✅ Test user created with ID: " + testUser.getId());
        System.out.println("✅ User count in DB: " + userJpaRepository.count());
    }

    @Test
    @DisplayName("GET /users/me - Success with real database")
    void getMyProfile_Success_WithRealDatabase() {
        // When
        var response = restTemplate.getForEntity("/users/me", String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("testuser@example.com");
        assertThat(response.getBody()).contains("Test User");
        assertThat(response.getBody()).contains("\"id\":" + testUser.getId());
    }

    @Test
    @DisplayName("PUT /users/me - Success (Update Name Only)")
    void updateMyProfile_Success_NameOnly() {
        // Given
        String requestBody = """
            {
                "name": "Updated Name"
            }
            """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        // When
        var response = restTemplate.exchange("/users/me", HttpMethod.PUT, request, String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Updated Name");
        assertThat(response.getBody()).contains("testuser@example.com");

        // Verify database was updated
        var updatedUser = userJpaRepository.findById(testUser.getId());
        assertThat(updatedUser).isPresent();
        assertThat(updatedUser.get().getName()).isEqualTo("Updated Name");
        assertThat(updatedUser.get().getEmail()).isEqualTo("testuser@example.com");
    }

    @Test
    @DisplayName("PUT /users/me - Success (Update Password Only)")
    void updateMyProfile_Success_PasswordOnly() {
        // Given
        String requestBody = """
            {
                "password": "newPassword456"
            }
            """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        String originalPasswordHash = testUser.getPassword();

        // When
        var response = restTemplate.exchange("/users/me", HttpMethod.PUT, request, String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Verify password was updated and encrypted
        var updatedUser = userJpaRepository.findById(testUser.getId());
        assertThat(updatedUser).isPresent();
        assertThat(updatedUser.get().getPassword()).isNotEqualTo(originalPasswordHash);
        assertThat(updatedUser.get().getPassword()).isNotEqualTo("newPassword456"); // Should be encrypted
        assertThat(updatedUser.get().getPassword()).startsWith("$2a$"); // BCrypt prefix

        // Verify new password works
        assertThat(passwordEncoder.matches("newPassword456", updatedUser.get().getPassword())).isTrue();
    }

    @Test
    @DisplayName("PUT /users/me - Success (Update Both Name and Password)")
    void updateMyProfile_Success_NameAndPassword() {
        // Given
        String requestBody = """
            {
                "name": "Completely New Name",
                "password": "superSecure999"
            }
            """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        // When
        var response = restTemplate.exchange("/users/me", HttpMethod.PUT, request, String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Completely New Name");

        // Verify both name and password were updated
        var updatedUser = userJpaRepository.findById(testUser.getId());
        assertThat(updatedUser).isPresent();
        assertThat(updatedUser.get().getName()).isEqualTo("Completely New Name");
        assertThat(passwordEncoder.matches("superSecure999", updatedUser.get().getPassword())).isTrue();
    }

    @Test
    @DisplayName("PUT /users/me - Success (Empty Body - No Changes)")
    void updateMyProfile_Success_EmptyBody() {
        // Given
        String requestBody = "{}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        String originalName = testUser.getName();
        String originalPassword = testUser.getPassword();

        // When
        var response = restTemplate.exchange("/users/me", HttpMethod.PUT, request, String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Verify nothing changed
        var unchangedUser = userJpaRepository.findById(testUser.getId());
        assertThat(unchangedUser).isPresent();
        assertThat(unchangedUser.get().getName()).isEqualTo(originalName);
        assertThat(unchangedUser.get().getPassword()).isEqualTo(originalPassword);
    }

    @Test
    @DisplayName("PUT /users/me - Validation Error (Name Too Short)")
    void updateMyProfile_ValidationError_ShortName() {
        // Given
        String requestBody = """
            {
                "name": "A"
            }
            """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        // When
        var response = restTemplate.exchange("/users/me", HttpMethod.PUT, request, String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        // Verify database was not updated
        var unchangedUser = userJpaRepository.findById(testUser.getId());
        assertThat(unchangedUser).isPresent();
        assertThat(unchangedUser.get().getName()).isEqualTo("Test User");
    }

    @Test
    @DisplayName("PUT /users/me - Validation Error (Name Too Long)")
    void updateMyProfile_ValidationError_LongName() {
        // Given
        String longName = "A".repeat(51); // 51 characters
        String requestBody = String.format("""
            {
                "name": "%s"
            }
            """, longName);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        // When
        var response = restTemplate.exchange("/users/me", HttpMethod.PUT, request, String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("PUT /users/me - Validation Error (Password Too Short)")
    void updateMyProfile_ValidationError_ShortPassword() {
        // Given
        String requestBody = """
            {
                "password": "short"
            }
            """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        // When
        var response = restTemplate.exchange("/users/me", HttpMethod.PUT, request, String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        // Verify password was not changed
        var unchangedUser = userJpaRepository.findById(testUser.getId());
        assertThat(unchangedUser).isPresent();
        assertThat(passwordEncoder.matches("password123", unchangedUser.get().getPassword())).isTrue();
    }

    @Test
    @DisplayName("PUT /users/me - Validation Error (Password Too Long)")
    void updateMyProfile_ValidationError_LongPassword() {
        // Given
        String longPassword = "A".repeat(101); // 101 characters
        String requestBody = String.format("""
            {
                "password": "%s"
            }
            """, longPassword);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        // When
        var response = restTemplate.exchange("/users/me", HttpMethod.PUT, request, String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("GET /users/me - Returns correct data structure")
    void getMyProfile_CorrectDataStructure() {
        // When
        var response = restTemplate.getForEntity("/users/me", String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        // Check JSON structure
        assertThat(response.getBody()).contains("\"id\":");
        assertThat(response.getBody()).contains("\"email\":");
        assertThat(response.getBody()).contains("\"name\":");
        assertThat(response.getBody()).contains("\"createdAt\":");
        // Password should NOT be exposed
        assertThat(response.getBody()).doesNotContain("\"password\":");
        assertThat(response.getBody()).doesNotContain("$2a$"); // No BCrypt hash
    }
}
