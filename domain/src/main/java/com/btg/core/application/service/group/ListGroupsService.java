package com.btg.core.application.service.group;

import com.btg.core.application.port.in.group.ListGroupsUseCase;
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
public class ListGroupsService implements ListGroupsUseCase {

    private final LoadGroupPort loadGroupPort;
    private final LoadUserPort loadUserPort;

    @Override
    public PagedGroupResult listGroups(ListGroupsQuery query) {
        List<LoadGroupPort.Group> groups;
        int totalCount;

        if ("MY".equals(query.type())) {
            groups = loadGroupPort.loadByUserId(query.userId(), query.page(), query.size());
            totalCount = loadGroupPort.countByUserId(query.userId());
        } else {
            groups = loadGroupPort.loadAll(query.page(), query.size());
            totalCount = loadGroupPort.countAll();
        }

        List<GroupSummary> groupSummaries = groups.stream()
                .map(group -> {
                    LoadUserPort.User creator = loadUserPort.loadById(group.createdBy())
                            .orElse(null);
                    UserInfo creatorInfo = creator != null
                            ? new UserInfo(creator.id(), creator.email(), creator.name())
                            : new UserInfo(group.createdBy(), "unknown", "Unknown User");

                    return new GroupSummary(
                            group.id(),
                            group.name(),
                            group.description(),
                            group.memberCount(),
                            group.maxMembers(),
                            creatorInfo,
                            group.createdAt()
                    );
                })
                .collect(Collectors.toList());

        int totalPages = (int) Math.ceil((double) totalCount / query.size());

        return new PagedGroupResult(
                groupSummaries,
                totalCount,
                totalPages,
                query.page(),
                query.size()
        );
    }
}
