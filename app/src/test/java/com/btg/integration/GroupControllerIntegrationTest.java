package com.btg.integration;

import com.btg.core.application.port.in.group.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Group Controller Integration Tests")
class GroupControllerIntegrationTest extends IntegrationTestBase {
    // Use Cases are inherited from IntegrationTestBase

    @Test
    @DisplayName("POST /groups - Success")
    void createGroup_Success() throws Exception {
        // Given
        CreateGroupUseCase.UserInfo userInfo = new CreateGroupUseCase.UserInfo(
            1L, "test@example.com", "Test User"
        );
        CreateGroupUseCase.GroupResult groupResult = new CreateGroupUseCase.GroupResult(
            1L,
            "Study Group",
            "Daily coding study",
            1,
            10,
            userInfo,
            "2025-01-01T00:00:00"
        );
        when(createGroupUseCase.createGroup(any())).thenReturn(groupResult);

        // When & Then
        mockMvc.perform(post("/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "name": "Study Group",
                        "description": "Daily coding study",
                        "maxMembers": 10
                    }
                    """))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Study Group"))
            .andExpect(jsonPath("$.description").value("Daily coding study"))
            .andExpect(jsonPath("$.memberCount").value(1))
            .andExpect(jsonPath("$.maxMembers").value(10))
            .andExpect(jsonPath("$.createdBy.id").value(1))
            .andExpect(jsonPath("$.createdBy.email").value("test@example.com"))
            .andExpect(jsonPath("$.createdBy.name").value("Test User"))
            .andExpect(jsonPath("$.createdAt").value("2025-01-01T00:00:00"));
    }

    @Test
    @DisplayName("POST /groups - Validation Error (Short Name)")
    void createGroup_ValidationError_ShortName() throws Exception {
        // When & Then
        mockMvc.perform(post("/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "name": "A",
                        "description": "Valid description"
                    }
                    """))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /groups - Validation Error (Long Description)")
    void createGroup_ValidationError_LongDescription() throws Exception {
        // When & Then
        mockMvc.perform(post("/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "name": "Valid Name",
                        "description": "%s"
                    }
                    """.formatted("A".repeat(501))))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /groups - Success (MY type)")
    void listGroups_Success_MyType() throws Exception {
        // Given
        ListGroupsUseCase.UserInfo userInfo = new ListGroupsUseCase.UserInfo(
            1L, "test@example.com", "Test User"
        );
        ListGroupsUseCase.GroupSummary group1 = new ListGroupsUseCase.GroupSummary(
            1L, "Group 1", "Description 1", 5, 10, userInfo, "2025-01-01T00:00:00"
        );
        ListGroupsUseCase.GroupSummary group2 = new ListGroupsUseCase.GroupSummary(
            2L, "Group 2", "Description 2", 3, 20, userInfo, "2025-01-02T00:00:00"
        );
        ListGroupsUseCase.PagedGroupResult pagedResult = new ListGroupsUseCase.PagedGroupResult(
            List.of(group1, group2),
            2,
            1,
            0,
            20
        );
        when(listGroupsUseCase.listGroups(any())).thenReturn(pagedResult);

        // When & Then
        mockMvc.perform(get("/groups")
                .param("type", "MY")
                .param("page", "0")
                .param("size", "20"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(2))
            .andExpect(jsonPath("$.content[0].id").value(1))
            .andExpect(jsonPath("$.content[0].name").value("Group 1"))
            .andExpect(jsonPath("$.content[1].id").value(2))
            .andExpect(jsonPath("$.content[1].name").value("Group 2"))
            .andExpect(jsonPath("$.totalElements").value(2))
            .andExpect(jsonPath("$.totalPages").value(1))
            .andExpect(jsonPath("$.page").value(0))
            .andExpect(jsonPath("$.size").value(20));
    }

    @Test
    @DisplayName("GET /groups/{groupId} - Success")
    void getGroup_Success() throws Exception {
        // Given
        GetGroupUseCase.UserInfo userInfo = new GetGroupUseCase.UserInfo(
            1L, "test@example.com", "Test User"
        );
        GetGroupUseCase.GroupDetailResult groupDetailResult = new GetGroupUseCase.GroupDetailResult(
            1L,
            "Study Group",
            "Daily coding study",
            5,
            10,
            userInfo,
            "2025-01-01T00:00:00",
            "MEMBER",
            3
        );
        when(getGroupUseCase.getGroup(1L)).thenReturn(groupDetailResult);

        // When & Then
        mockMvc.perform(get("/groups/1"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Study Group"))
            .andExpect(jsonPath("$.description").value("Daily coding study"))
            .andExpect(jsonPath("$.memberCount").value(5))
            .andExpect(jsonPath("$.maxMembers").value(10))
            .andExpect(jsonPath("$.myRole").value("MEMBER"))
            .andExpect(jsonPath("$.taskCount").value(3))
            .andExpect(jsonPath("$.createdBy.id").value(1))
            .andExpect(jsonPath("$.createdAt").value("2025-01-01T00:00:00"));
    }

    @Test
    @DisplayName("PUT /groups/{groupId} - Success")
    void updateGroup_Success() throws Exception {
        // Given
        UpdateGroupUseCase.UserInfo userInfo = new UpdateGroupUseCase.UserInfo(
            1L, "test@example.com", "Test User"
        );
        UpdateGroupUseCase.GroupResult groupResult = new UpdateGroupUseCase.GroupResult(
            1L,
            "Updated Name",
            "Updated description",
            5,
            15,
            userInfo,
            "2025-01-01T00:00:00"
        );
        when(updateGroupUseCase.updateGroup(any())).thenReturn(groupResult);

        // When & Then
        mockMvc.perform(put("/groups/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "name": "Updated Name",
                        "description": "Updated description",
                        "maxMembers": 15
                    }
                    """))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Updated Name"))
            .andExpect(jsonPath("$.description").value("Updated description"))
            .andExpect(jsonPath("$.maxMembers").value(15));
    }

    @Test
    @DisplayName("PUT /groups/{groupId} - Validation Error (Short Name)")
    void updateGroup_ValidationError_ShortName() throws Exception {
        // When & Then
        mockMvc.perform(put("/groups/1")
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
    @DisplayName("DELETE /groups/{groupId} - Success")
    void deleteGroup_Success() throws Exception {
        // Given
        doNothing().when(deleteGroupUseCase).deleteGroup(any());

        // When & Then
        mockMvc.perform(delete("/groups/1"))
            .andDo(print())
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("POST /groups/{groupId}/members - Success")
    void joinGroup_Success() throws Exception {
        // Given
        JoinGroupUseCase.UserInfo userInfo = new JoinGroupUseCase.UserInfo(
            1L, "test@example.com", "Test User"
        );
        JoinGroupUseCase.GroupMemberResult groupMemberResult = new JoinGroupUseCase.GroupMemberResult(
            1L,
            userInfo,
            "MEMBER",
            "2025-01-01T00:00:00"
        );
        when(joinGroupUseCase.joinGroup(any())).thenReturn(groupMemberResult);

        // When & Then
        mockMvc.perform(post("/groups/1/members"))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.user.id").value(1))
            .andExpect(jsonPath("$.user.email").value("test@example.com"))
            .andExpect(jsonPath("$.user.name").value("Test User"))
            .andExpect(jsonPath("$.role").value("MEMBER"))
            .andExpect(jsonPath("$.joinedAt").value("2025-01-01T00:00:00"));
    }
}
