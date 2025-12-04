package com.btg.core.application.service.group;

import com.btg.core.application.port.in.group.SearchGroupsUseCase;
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
public class SearchGroupsService implements SearchGroupsUseCase {

    private final LoadGroupPort loadGroupPort;
    private final LoadUserPort loadUserPort;

    @Override
    public PagedGroupResult searchGroups(SearchGroupsQuery query) {
        List<LoadGroupPort.Group> groups = loadGroupPort.searchByKeyword(
                query.keyword(), query.page(), query.size());
        int totalCount = loadGroupPort.countByKeyword(query.keyword());

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
