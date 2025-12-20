package com.btg.core.application.service.group;

import com.btg.core.application.port.in.group.GetGroupUseCase;
import com.btg.core.application.port.out.group.LoadGroupMemberPort;
import com.btg.core.application.port.out.group.LoadGroupPort;
import com.btg.core.application.port.out.user.LoadUserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetGroupService implements GetGroupUseCase {

    private final LoadGroupPort loadGroupPort;
    private final LoadGroupMemberPort loadGroupMemberPort;
    private final LoadUserPort loadUserPort;

    @Override
    public GroupDetailResult getGroup(Long groupId) {
        LoadGroupPort.Group group = loadGroupPort.loadById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found: " + groupId));

        LoadUserPort.User creator = loadUserPort.loadById(group.createdBy())
                .orElseThrow(() -> new IllegalArgumentException("Creator not found: " + group.createdBy()));

        return new GroupDetailResult(
                group.id(),
                group.name(),
                group.description(),
                group.memberCount(),
                group.maxMembers(),
                new UserInfo(creator.id(), creator.email(), creator.name()),
                group.createdAt(),
                "NONE",
                0
        );
    }
}
