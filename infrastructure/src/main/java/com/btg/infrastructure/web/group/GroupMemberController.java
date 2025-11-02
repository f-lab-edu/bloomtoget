package com.btg.infrastructure.web.group;

import com.btg.core.application.port.in.group.JoinGroupUseCase;
import com.btg.infrastructure.web.group.dto.response.GroupMemberResponse;
import com.btg.infrastructure.web.group.dto.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/groups/{groupId}/members")
@RequiredArgsConstructor
public class GroupMemberController {

    private final JoinGroupUseCase joinGroupUseCase;

    // TODO: GET /groups/{groupId}/members - 그룹 멤버 목록

    @PostMapping
    public ResponseEntity<GroupMemberResponse> joinGroup(@PathVariable Long groupId) {
        // TODO: Get authenticated user ID from SecurityContext
        Long userId = 1L;

        JoinGroupUseCase.JoinGroupCommand command = new JoinGroupUseCase.JoinGroupCommand(
            groupId,
            userId
        );

        JoinGroupUseCase.GroupMemberResult result = joinGroupUseCase.joinGroup(command);

        GroupMemberResponse response = new GroupMemberResponse(
            result.id(),
            new UserResponse(
                result.user().id(),
                result.user().email(),
                result.user().name()
            ),
            result.role(),
            result.joinedAt()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // TODO: DELETE /groups/{groupId}/members/me - 그룹 탈퇴
}
