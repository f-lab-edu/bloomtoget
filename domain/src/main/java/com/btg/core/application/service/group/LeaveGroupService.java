package com.btg.core.application.service.group;

import com.btg.core.application.port.in.group.LeaveGroupUseCase;
import com.btg.core.application.port.out.group.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LeaveGroupService implements LeaveGroupUseCase {

    private final LoadGroupPort loadGroupPort;
    private final LoadGroupMemberPort loadGroupMemberPort;
    private final DeleteGroupMemberPort deleteGroupMemberPort;
    private final UpdateGroupMemberPort updateGroupMemberPort;
    private final DeleteGroupPort deleteGroupPort;

    @Override
    public void leaveGroup(LeaveGroupCommand command) {
        // 그룹 존재 확인
        loadGroupPort.loadById(command.groupId())
                .orElseThrow(() -> new IllegalArgumentException("Group not found: " + command.groupId()));

        // 멤버십 확인
        LoadGroupMemberPort.GroupMember member = loadGroupMemberPort
                .loadByGroupIdAndUserId(command.groupId(), command.userId())
                .orElseThrow(() -> new IllegalArgumentException("User is not a member of this group"));

        int memberCount = loadGroupMemberPort.countByGroupId(command.groupId());

        if ("ADMIN".equals(member.role())) {
            if (memberCount == 1) {
                // 유일한 멤버인 경우 그룹 삭제
                deleteGroupPort.delete(command.groupId());
            } else {
                // 다른 멤버가 있는 경우 가장 오래된 MEMBER에게 ADMIN 위임
                LoadGroupMemberPort.GroupMember newAdmin = loadGroupMemberPort
                        .loadOldestMemberByGroupId(command.groupId(), command.userId())
                        .orElseThrow(() -> new IllegalStateException(
                                "No member available to transfer admin role"));

                updateGroupMemberPort.updateRole(newAdmin.id(), "ADMIN");
                deleteGroupMemberPort.deleteByGroupIdAndUserId(command.groupId(), command.userId());
            }
        } else {
            // 일반 멤버인 경우 바로 탈퇴
            deleteGroupMemberPort.deleteByGroupIdAndUserId(command.groupId(), command.userId());
        }
    }
}
