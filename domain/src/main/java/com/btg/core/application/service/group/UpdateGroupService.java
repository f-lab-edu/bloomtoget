package com.btg.core.application.service.group;

import com.btg.core.application.port.in.group.UpdateGroupUseCase;
import com.btg.core.application.port.out.group.LoadGroupMemberPort;
import com.btg.core.application.port.out.group.LoadGroupPort;
import com.btg.core.application.port.out.group.UpdateGroupPort;
import com.btg.core.application.port.out.user.LoadUserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateGroupService implements UpdateGroupUseCase {

    private final LoadGroupPort loadGroupPort;
    private final LoadGroupMemberPort loadGroupMemberPort;
    private final UpdateGroupPort updateGroupPort;
    private final LoadUserPort loadUserPort;

    @Override
    public GroupResult updateGroup(UpdateGroupCommand command) {
        LoadGroupPort.Group group = loadGroupPort.loadById(command.groupId())
                .orElseThrow(() -> new IllegalArgumentException("Group not found: " + command.groupId()));

        LoadGroupMemberPort.GroupMember member = loadGroupMemberPort
                .loadByGroupIdAndUserId(command.groupId(), command.userId())
                .orElseThrow(() -> new IllegalArgumentException("User is not a member of this group"));

        if (!"ADMIN".equals(member.role())) {
            throw new IllegalArgumentException("Only ADMIN can update the group");
        }

        if (command.maxMembers() != null && command.maxMembers() < group.memberCount()) {
            throw new IllegalArgumentException(
                    "Max members (" + command.maxMembers() + ") cannot be less than current member count (" + group.memberCount() + ")"
            );
        }

        UpdateGroupPort.Group updatedGroup = updateGroupPort.update(
                command.groupId(),
                command.name(),
                command.description(),
                command.maxMembers()
        );

        LoadUserPort.User creator = loadUserPort.loadById(updatedGroup.createdBy())
                .orElseThrow(() -> new IllegalArgumentException("Creator not found: " + updatedGroup.createdBy()));

        return new GroupResult(
                updatedGroup.id(),
                updatedGroup.name(),
                updatedGroup.description(),
                updatedGroup.memberCount(),
                updatedGroup.maxMembers(),
                new UserInfo(creator.id(), creator.email(), creator.name()),
                updatedGroup.createdAt()
        );
    }
}
