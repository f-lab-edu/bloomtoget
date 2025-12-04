package com.btg.core.application.service.group;

import com.btg.core.application.port.in.group.DeleteGroupUseCase;
import com.btg.core.application.port.out.group.DeleteGroupPort;
import com.btg.core.application.port.out.group.LoadGroupMemberPort;
import com.btg.core.application.port.out.group.LoadGroupPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DeleteGroupService implements DeleteGroupUseCase {

    private final LoadGroupPort loadGroupPort;
    private final LoadGroupMemberPort loadGroupMemberPort;
    private final DeleteGroupPort deleteGroupPort;

    @Override
    public void deleteGroup(DeleteGroupCommand command) {
        loadGroupPort.loadById(command.groupId())
                .orElseThrow(() -> new IllegalArgumentException("Group not found: " + command.groupId()));

        LoadGroupMemberPort.GroupMember member = loadGroupMemberPort
                .loadByGroupIdAndUserId(command.groupId(), command.userId())
                .orElseThrow(() -> new IllegalArgumentException("User is not a member of this group"));

        if (!"ADMIN".equals(member.role())) {
            throw new IllegalArgumentException("Only ADMIN can delete the group");
        }

        deleteGroupPort.delete(command.groupId());
    }
}
