package com.btg.integration;

import com.btg.core.application.port.in.user.GetUserProfileUseCase;
import com.btg.core.application.port.in.user.UpdateUserProfileUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("User Controller Integration Tests")
class UserControllerIntegrationTest extends IntegrationTestBase {
    // Use Cases are inherited from IntegrationTestBase

    @Test
    @DisplayName("GET /users/me - Success")
    void getMyProfile_Success() throws Exception {
        // Given
        var userProfile = new GetUserProfileUseCase.UserProfileResult(
            1L,
            "test@example.com",
            "Test User",
            "2025-01-01T00:00:00"
        );
        when(getUserProfileUseCase.getUserProfile(eq(1L))).thenReturn(userProfile);

        // When & Then
        mockMvc.perform(get("/users/me"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.email").value("test@example.com"))
            .andExpect(jsonPath("$.name").value("Test User"))
            .andExpect(jsonPath("$.createdAt").value("2025-01-01T00:00:00"));
    }

    @Test
    @DisplayName("PUT /users/me - Success (Update Name Only)")
    void updateMyProfile_Success_NameOnly() throws Exception {
        // Given
        var updatedProfile = new UpdateUserProfileUseCase.UserProfileResult(
            1L,
            "test@example.com",
            "Updated Name",
            "2025-01-01T00:00:00"
        );
        when(updateUserProfileUseCase.updateUserProfile(any())).thenReturn(updatedProfile);

        // When & Then
        mockMvc.perform(put("/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "name": "Updated Name"
                    }
                    """))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.email").value("test@example.com"))
            .andExpect(jsonPath("$.name").value("Updated Name"))
            .andExpect(jsonPath("$.createdAt").value("2025-01-01T00:00:00"));
    }

    @Test
    @DisplayName("PUT /users/me - Success (Update Name and Password)")
    void updateMyProfile_Success_NameAndPassword() throws Exception {
        // Given
        var updatedProfile = new UpdateUserProfileUseCase.UserProfileResult(
            1L,
            "test@example.com",
            "Updated Name",
            "2025-01-01T00:00:00"
        );
        when(updateUserProfileUseCase.updateUserProfile(any())).thenReturn(updatedProfile);

        // When & Then
        mockMvc.perform(put("/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "name": "Updated Name",
                        "password": "newPassword123"
                    }
                    """))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    @DisplayName("PUT /users/me - Validation Error (Short Name)")
    void updateMyProfile_ValidationError_ShortName() throws Exception {
        // When & Then
        mockMvc.perform(put("/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "name": "A"
                    }
                    """))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /users/me - Validation Error (Short Password)")
    void updateMyProfile_ValidationError_ShortPassword() throws Exception {
        // When & Then
        mockMvc.perform(put("/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "name": "Valid Name",
                        "password": "short"
                    }
                    """))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /users/me - Success (Empty Body - All Fields Optional)")
    void updateMyProfile_Success_EmptyBody() throws Exception {
        // Given
        var updatedProfile = new UpdateUserProfileUseCase.UserProfileResult(
            1L,
            "test@example.com",
            "Test User",
            "2025-01-01T00:00:00"
        );
        when(updateUserProfileUseCase.updateUserProfile(any())).thenReturn(updatedProfile);

        // When & Then
        mockMvc.perform(put("/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.email").value("test@example.com"))
            .andExpect(jsonPath("$.name").value("Test User"));
    }
}
