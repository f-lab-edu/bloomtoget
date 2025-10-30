package com.btg.integration;

import com.btg.core.application.port.in.auth.*;
import com.btg.core.application.port.in.dailyprogress.*;
import com.btg.core.application.port.in.group.*;
import com.btg.core.application.port.in.task.*;
import com.btg.core.application.port.in.user.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Base class for integration tests
 *
 * Features:
 * - Loads full Spring application context
 * - Provides MockMvc for HTTP request testing
 * - Uses test profile
 * - Security disabled for testing
 * - All Use Cases are mocked (no real implementation needed)
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class IntegrationTestBase {

    @Autowired
    protected MockMvc mockMvc;

    // Auth Use Cases
    @MockitoBean
    protected SignupUseCase signupUseCase;

    @MockitoBean
    protected LoginUseCase loginUseCase;

    @MockitoBean
    protected RefreshTokenUseCase refreshTokenUseCase;

    @MockitoBean
    protected LogoutUseCase logoutUseCase;

    // User Use Cases
    @MockitoBean
    protected GetUserProfileUseCase getUserProfileUseCase;

    @MockitoBean
    protected UpdateUserProfileUseCase updateUserProfileUseCase;

    // Group Use Cases
    @MockitoBean
    protected CreateGroupUseCase createGroupUseCase;

    @MockitoBean
    protected GetGroupUseCase getGroupUseCase;

    @MockitoBean
    protected UpdateGroupUseCase updateGroupUseCase;

    @MockitoBean
    protected DeleteGroupUseCase deleteGroupUseCase;

    @MockitoBean
    protected ListGroupsUseCase listGroupsUseCase;

    @MockitoBean
    protected JoinGroupUseCase joinGroupUseCase;

    // Task Use Cases
    @MockitoBean
    protected CreateTaskUseCase createTaskUseCase;

    @MockitoBean
    protected GetTaskUseCase getTaskUseCase;

    @MockitoBean
    protected UpdateTaskUseCase updateTaskUseCase;

    @MockitoBean
    protected DeleteTaskUseCase deleteTaskUseCase;

    @MockitoBean
    protected ListTasksUseCase listTasksUseCase;

    // DailyProgress Use Cases
    @MockitoBean
    protected GetDailyProgressUseCase getDailyProgressUseCase;

    @MockitoBean
    protected UpdateDailyProgressUseCase updateDailyProgressUseCase;
}
