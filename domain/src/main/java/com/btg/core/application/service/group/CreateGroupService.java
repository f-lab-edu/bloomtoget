package com.btg.core.application.service.group;

import com.btg.core.application.port.in.group.CreateGroupUseCase;
import com.btg.core.application.port.out.group.SaveGroupMemberPort;
import com.btg.core.application.port.out.group.SaveGroupPort;
import com.btg.core.application.port.out.user.LoadUserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateGroupService implements CreateGroupUseCase {

    private final SaveGroupPort saveGroupPort;
    private final SaveGroupMemberPort saveGroupMemberPort;
    private final LoadUserPort loadUserPort;

    @Override
    public GroupResult createGroup(CreateGroupCommand command) {
        LoadUserPort.User user = loadUserPort.loadById(command.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + command.userId()));

        SaveGroupPort.Group savedGroup = saveGroupPort.save(
                command.name(),
                command.description(),
                command.maxMembers(),
                command.userId()
        );

        saveGroupMemberPort.save(savedGroup.id(), command.userId(), "ADMIN");

        return new GroupResult(
                savedGroup.id(),
                savedGroup.name(),
                savedGroup.description(),
                1,
                savedGroup.maxMembers(),
                new UserInfo(user.id(), user.email(), user.name()),
                savedGroup.createdAt()
        );
    }
}
