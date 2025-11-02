package com.btg.infrastructure.web.group;

import com.btg.core.application.port.in.group.*;
import com.btg.infrastructure.web.group.dto.request.CreateGroupRequest;
import com.btg.infrastructure.web.group.dto.request.UpdateGroupRequest;
import com.btg.infrastructure.web.group.dto.response.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
public class GroupController {

    private final CreateGroupUseCase createGroupUseCase;
    private final GetGroupUseCase getGroupUseCase;
    private final UpdateGroupUseCase updateGroupUseCase;
    private final DeleteGroupUseCase deleteGroupUseCase;
    private final ListGroupsUseCase listGroupsUseCase;

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

        GroupResponse response = new GroupResponse(
            result.id(),
            result.name(),
            result.description(),
            result.memberCount(),
            result.maxMembers(),
            new UserResponse(
                result.createdBy().id(),
                result.createdBy().email(),
                result.createdBy().name()
            ),
            result.createdAt()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
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

        PagedGroupResponse response = new PagedGroupResponse(
            result.content().stream()
                .map(group -> new GroupResponse(
                    group.id(),
                    group.name(),
                    group.description(),
                    group.memberCount(),
                    group.maxMembers(),
                    new UserResponse(
                        group.createdBy().id(),
                        group.createdBy().email(),
                        group.createdBy().name()
                    ),
                    group.createdAt()
                ))
                .collect(Collectors.toList()),
            result.totalElements(),
            result.totalPages(),
            result.page(),
            result.size()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<GroupDetailResponse> getGroup(@PathVariable Long groupId) {
        GetGroupUseCase.GroupDetailResult result = getGroupUseCase.getGroup(groupId);

        GroupDetailResponse response = new GroupDetailResponse(
            result.id(),
            result.name(),
            result.description(),
            result.memberCount(),
            result.maxMembers(),
            new UserResponse(
                result.createdBy().id(),
                result.createdBy().email(),
                result.createdBy().name()
            ),
            result.createdAt(),
            result.myRole(),
            result.taskCount()
        );

        return ResponseEntity.ok(response);
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

        GroupResponse response = new GroupResponse(
            result.id(),
            result.name(),
            result.description(),
            result.memberCount(),
            result.maxMembers(),
            new UserResponse(
                result.createdBy().id(),
                result.createdBy().email(),
                result.createdBy().name()
            ),
            result.createdAt()
        );

        return ResponseEntity.ok(response);
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
