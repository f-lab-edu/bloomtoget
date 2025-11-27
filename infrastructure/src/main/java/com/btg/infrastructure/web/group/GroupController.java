package com.btg.infrastructure.web.group;

import com.btg.core.application.port.in.group.*;
import com.btg.infrastructure.web.group.dto.request.CreateGroupRequest;
import com.btg.infrastructure.web.group.dto.request.UpdateGroupRequest;
import com.btg.infrastructure.web.group.dto.response.*;
import com.btg.infrastructure.web.mapper.GroupResponseMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
public class GroupController {

    private final CreateGroupUseCase createGroupUseCase;
    private final GetGroupUseCase getGroupUseCase;
    private final UpdateGroupUseCase updateGroupUseCase;
    private final DeleteGroupUseCase deleteGroupUseCase;
    private final ListGroupsUseCase listGroupsUseCase;

    private final GroupResponseMapper groupResponseMapper;

    @PostMapping
    public ResponseEntity<GroupResponse> createGroup(@Valid @RequestBody CreateGroupRequest request) {
        // TODO: Get authenticated user ID from SecurityContext
        Long userId = 1L;

        CreateGroupUseCase.CreateGroupCommand command = new CreateGroupUseCase.CreateGroupCommand(
            userId,
            request.name(),
            request.description(),
            request.maxMembers()
        );

        CreateGroupUseCase.GroupResult result = createGroupUseCase.createGroup(command);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(groupResponseMapper.toResponse(result));
    }

    // TODO: GET /groups/search - 그룹 검색

    @GetMapping
    public ResponseEntity<PagedGroupResponse> listGroups(
        @RequestParam(defaultValue = "MY") String type,
        @RequestParam(defaultValue = "0") Integer page,
        @RequestParam(defaultValue = "20") Integer size
    ) {
        // TODO: Get authenticated user ID from SecurityContext
        Long userId = 1L;

        ListGroupsUseCase.ListGroupsQuery query = new ListGroupsUseCase.ListGroupsQuery(
            userId,
            type,
            page,
            size
        );

        ListGroupsUseCase.PagedGroupResult result = listGroupsUseCase.listGroups(query);

        return ResponseEntity.ok(groupResponseMapper.toPagedResponse(result));
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<GroupDetailResponse> getGroup(@PathVariable Long groupId) {
        GetGroupUseCase.GroupDetailResult result = getGroupUseCase.getGroup(groupId);

        return ResponseEntity.ok(groupResponseMapper.toDetailResponse(result));
    }

    @PutMapping("/{groupId}")
    public ResponseEntity<GroupResponse> updateGroup(
        @PathVariable Long groupId,
        @Valid @RequestBody UpdateGroupRequest request
    ) {
        // TODO: Get authenticated user ID from SecurityContext
        Long userId = 1L;

        UpdateGroupUseCase.UpdateGroupCommand command = new UpdateGroupUseCase.UpdateGroupCommand(
            groupId,
            userId,
            request.name(),
            request.description(),
            request.maxMembers()
        );

        UpdateGroupUseCase.GroupResult result = updateGroupUseCase.updateGroup(command);

        return ResponseEntity.ok(groupResponseMapper.toResponse(result));
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long groupId) {
        // TODO: Get authenticated user ID from SecurityContext
        Long userId = 1L;

        DeleteGroupUseCase.DeleteGroupCommand command = new DeleteGroupUseCase.DeleteGroupCommand(
            groupId,
            userId
        );

        deleteGroupUseCase.deleteGroup(command);

        return ResponseEntity.noContent().build();
    }
}
