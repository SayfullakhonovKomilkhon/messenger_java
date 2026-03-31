package com.messenger.e2ee.repository;

import com.messenger.e2ee.entity.IdentityKeyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IdentityKeyRepository extends JpaRepository<IdentityKeyEntity, UUID> {
}
