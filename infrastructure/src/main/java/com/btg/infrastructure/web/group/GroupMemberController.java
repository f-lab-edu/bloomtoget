package com.btg.infrastructure.web.group;

import com.btg.core.application.port.in.group.JoinGroupUseCase;
import com.btg.core.application.port.in.group.LeaveGroupUseCase;
import com.btg.core.application.port.in.group.ListGroupMembersUseCase;
import com.btg.infrastructure.web.group.dto.response.GroupMemberListResponse;
import com.btg.infrastructure.web.group.dto.response.GroupMemberResponse;
import com.btg.infrastructure.web.group.dto.response.UserResponse;
import com.btg.infrastructure.web.mapper.GroupResponseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/groups/{groupId}/members")
@RequiredArgsConstructor
public class GroupMemberController {

    private final JoinGroupUseCase joinGroupUseCase;
    private final ListGroupMembersUseCase listGroupMembersUseCase;
    private final LeaveGroupUseCase leaveGroupUseCase;
    private final GroupResponseMapper groupResponseMapper;

    @GetMapping
    public ResponseEntity<GroupMemberListResponse> listGroupMembers(@PathVariable Long groupId) {
        ListGroupMembersUseCase.ListGroupMembersQuery query =
            new ListGroupMembersUseCase.ListGroupMembersQuery(groupId);

        ListGroupMembersUseCase.GroupMemberListResult result =
            listGroupMembersUseCase.listGroupMembers(query);

        return ResponseEntity.ok(groupResponseMapper.toMemberListResponse(result));
    }

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

    @DeleteMapping("/me")
    public ResponseEntity<Void> leaveGroup(@PathVariable Long groupId) {
        // TODO: Get authenticated user ID from SecurityContext
        Long userId = 1L;

        LeaveGroupUseCase.LeaveGroupCommand command =
            new LeaveGroupUseCase.LeaveGroupCommand(groupId, userId);

        leaveGroupUseCase.leaveGroup(command);

        return ResponseEntity.noContent().build();
    }
}
