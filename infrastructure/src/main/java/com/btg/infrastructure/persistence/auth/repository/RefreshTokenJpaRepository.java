package com.btg.infrastructure.persistence.auth.repository;

import com.btg.infrastructure.persistence.auth.entity.RefreshTokenJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenJpaEntity, Long> {

    Optional<RefreshTokenJpaEntity> findByToken(String token);

    void deleteByToken(String token);

    void deleteByUserId(Long userId);
}
