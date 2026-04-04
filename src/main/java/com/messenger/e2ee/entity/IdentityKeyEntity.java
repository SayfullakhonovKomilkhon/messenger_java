package com.messenger.e2ee.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "e2ee_identity_keys")
public class IdentityKeyEntity {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "registration_id", nullable = false)
    private int registrationId;

    @Column(name = "identity_public_key", nullable = false)
    private byte[] identityPublicKey;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public int getRegistrationId() { return registrationId; }
    public void setRegistrationId(int registrationId) { this.registrationId = registrationId; }

    public byte[] getIdentityPublicKey() { return identityPublicKey; }
    public void setIdentityPublicKey(byte[] identityPublicKey) { this.identityPublicKey = identityPublicKey; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}
