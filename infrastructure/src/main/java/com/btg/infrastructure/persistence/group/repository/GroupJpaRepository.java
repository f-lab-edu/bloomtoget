package com.btg.infrastructure.persistence.group.repository;

import com.btg.infrastructure.persistence.group.entity.GroupJpaEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GroupJpaRepository extends JpaRepository<GroupJpaEntity, Long> {

    @Query("""
        SELECT g FROM GroupJpaEntity g
        JOIN GroupMemberJpaEntity gm ON g.id = gm.groupId
        WHERE gm.userId = :userId
        ORDER BY g.createdAt DESC
        """)
    List<GroupJpaEntity> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("""
        SELECT COUNT(g) FROM GroupJpaEntity g
        JOIN GroupMemberJpaEntity gm ON g.id = gm.groupId
        WHERE gm.userId = :userId
        """)
    int countByUserId(@Param("userId") Long userId);

    List<GroupJpaEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query("""
        SELECT g FROM GroupJpaEntity g
        WHERE LOWER(g.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(g.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
        ORDER BY g.createdAt DESC
        """)
    List<GroupJpaEntity> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("""
        SELECT COUNT(g) FROM GroupJpaEntity g
        WHERE LOWER(g.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(g.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
        """)
    int countByKeyword(@Param("keyword") String keyword);
}
