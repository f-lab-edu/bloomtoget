package com.btg.integration;

import com.btg.core.application.port.in.auth.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Auth Controller Integration Tests")
class AuthControllerIntegrationTest extends IntegrationTestBase {
    // Use Cases are inherited from IntegrationTestBase

    @Test
    @DisplayName("POST /auth/signup - Success")
    void signup_Success() throws Exception {
        // Given
        var userResult = new SignupUseCase.UserResult(
            1L,
            "test@example.com",
            "Test User",
            "2025-01-01T00:00:00"
        );
        when(signupUseCase.signup(any())).thenReturn(userResult);

        // When & Then
        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "email": "test@example.com",
                        "password": "password123",
                        "name": "Test User"
                    }
                    """))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.email").value("test@example.com"))
            .andExpect(jsonPath("$.name").value("Test User"))
            .andExpect(jsonPath("$.createdAt").value("2025-01-01T00:00:00"));
    }

    @Test
    @DisplayName("POST /auth/signup - Validation Error (Invalid Email)")
    void signup_ValidationError_InvalidEmail() throws Exception {
        // When & Then
        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "email": "invalid-email",
                        "password": "password123",
                        "name": "Test User"
                    }
                    """))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /auth/signup - Validation Error (Short Password)")
    void signup_ValidationError_ShortPassword() throws Exception {
        // When & Then
        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "email": "test@example.com",
                        "password": "short",
                        "name": "Test User"
                    }
                    """))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /auth/login - Success")
    void login_Success() throws Exception {
        // Given
        var userInfo = new LoginUseCase.LoginResult.UserInfo(
            1L,
            "test@example.com",
            "Test User",
            "2025-01-01T00:00:00"
        );
        var loginResult = new LoginUseCase.LoginResult(
            "dummy-access-token",
            "dummy-refresh-token",
            userInfo
        );
        when(loginUseCase.login(any())).thenReturn(loginResult);

        // When & Then
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "email": "test@example.com",
                        "password": "password123"
                    }
                    """))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").value("dummy-access-token"))
            .andExpect(jsonPath("$.refreshToken").value("dummy-refresh-token"))
            .andExpect(jsonPath("$.user.id").value(1))
            .andExpect(jsonPath("$.user.email").value("test@example.com"))
            .andExpect(jsonPath("$.user.name").value("Test User"));
    }

    @Test
    @DisplayName("POST /auth/login - Validation Error (Missing Email)")
    void login_ValidationError_MissingEmail() throws Exception {
        // When & Then
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "password": "password123"
                    }
                    """))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /auth/refresh - Success")
    void refreshToken_Success() throws Exception {
        // Given
        var tokenResult = new RefreshTokenUseCase.TokenResult("new-access-token");
        when(refreshTokenUseCase.refreshToken(any())).thenReturn(tokenResult);

        // When & Then
        mockMvc.perform(post("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "refreshToken": "dummy-refresh-token"
                    }
                    """))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").value("new-access-token"));
    }

    @Test
    @DisplayName("POST /auth/refresh - Validation Error (Missing Refresh Token)")
    void refreshToken_ValidationError_MissingToken() throws Exception {
        // When & Then
        mockMvc.perform(post("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /auth/logout - Success")
    void logout_Success() throws Exception {
        // Given
        doNothing().when(logoutUseCase).logout(any());

        // When & Then
        mockMvc.perform(post("/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "refreshToken": "dummy-refresh-token"
                    }
                    """))
            .andDo(print())
            .andExpect(status().isNoContent());
    }
}
