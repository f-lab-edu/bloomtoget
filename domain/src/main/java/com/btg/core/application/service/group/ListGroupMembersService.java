package com.btg.core.application.service.group;

import com.btg.core.application.port.in.group.ListGroupMembersUseCase;
import com.btg.core.application.port.out.group.LoadGroupMemberPort;
import com.btg.core.application.port.out.group.LoadGroupPort;
import com.btg.core.application.port.out.user.LoadUserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListGroupMembersService implements ListGroupMembersUseCase {

    private final LoadGroupPort loadGroupPort;
    private final LoadGroupMemberPort loadGroupMemberPort;
    private final LoadUserPort loadUserPort;

    @Override
    public GroupMemberListResult listGroupMembers(ListGroupMembersQuery query) {
        // 그룹 존재 확인
        loadGroupPort.loadById(query.groupId())
                .orElseThrow(() -> new IllegalArgumentException("Group not found: " + query.groupId()));

        List<LoadGroupMemberPort.GroupMember> members = loadGroupMemberPort.loadByGroupId(query.groupId());

        List<GroupMemberInfo> memberInfos = members.stream()
                .map(member -> {
                    LoadUserPort.User user = loadUserPort.loadById(member.userId())
                            .orElse(null);
                    UserInfo userInfo = user != null
                            ? new UserInfo(user.id(), user.email(), user.name())
                            : new UserInfo(member.userId(), "unknown", "Unknown User");

                    return new GroupMemberInfo(
                            member.id(),
                            userInfo,
                            member.role(),
                            member.joinedAt()
                    );
                })
                .collect(Collectors.toList());

        return new GroupMemberListResult(memberInfos, memberInfos.size());
    }
}
