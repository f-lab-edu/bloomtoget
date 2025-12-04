package com.btg.core.application.service.group;

import com.btg.core.application.port.in.group.JoinGroupUseCase;
import com.btg.core.application.port.out.group.LoadGroupMemberPort;
import com.btg.core.application.port.out.group.LoadGroupPort;
import com.btg.core.application.port.out.group.SaveGroupMemberPort;
import com.btg.core.application.port.out.user.LoadUserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class JoinGroupService implements JoinGroupUseCase {

    private final LoadGroupPort loadGroupPort;
    private final LoadGroupMemberPort loadGroupMemberPort;
    private final SaveGroupMemberPort saveGroupMemberPort;
    private final LoadUserPort loadUserPort;

    @Override
    public GroupMemberResult joinGroup(JoinGroupCommand command) {
        LoadGroupPort.Group group = loadGroupPort.loadById(command.groupId())
                .orElseThrow(() -> new IllegalArgumentException("Group not found: " + command.groupId()));

        LoadUserPort.User user = loadUserPort.loadById(command.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + command.userId()));

        if (loadGroupMemberPort.existsByGroupIdAndUserId(command.groupId(), command.userId())) {
            throw new IllegalArgumentException("User is already a member of this group");
        }

        int currentMemberCount = loadGroupMemberPort.countByGroupId(command.groupId());
        if (currentMemberCount >= group.maxMembers()) {
            throw new IllegalArgumentException("Group is full (max: " + group.maxMembers() + ")");
        }

        SaveGroupMemberPort.GroupMember savedMember = saveGroupMemberPort.save(
                command.groupId(),
                command.userId(),
                "MEMBER"
        );

        return new GroupMemberResult(
                savedMember.id(),
                new UserInfo(user.id(), user.email(), user.name()),
                savedMember.role(),
                savedMember.joinedAt()
        );
    }
}
