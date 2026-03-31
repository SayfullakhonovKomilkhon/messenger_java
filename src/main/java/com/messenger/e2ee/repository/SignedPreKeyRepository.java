package com.messenger.e2ee.repository;

import com.messenger.e2ee.entity.SignedPreKeyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SignedPreKeyRepository extends JpaRepository<SignedPreKeyEntity, UUID> {
    Optional<SignedPreKeyEntity> findTopByUserIdOrderByCreatedAtDesc(UUID userId);
    void deleteAllByUserId(UUID userId);
}
