package com.messenger.e2ee.repository;

import com.messenger.e2ee.entity.PreKeyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PreKeyRepository extends JpaRepository<PreKeyEntity, UUID> {
    Optional<PreKeyEntity> findFirstByUserIdAndUsedFalse(UUID userId);
    long countByUserIdAndUsedFalse(UUID userId);
    void deleteAllByUserId(UUID userId);
}
