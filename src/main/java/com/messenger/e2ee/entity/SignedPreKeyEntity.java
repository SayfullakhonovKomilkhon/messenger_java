package com.messenger.e2ee.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "e2ee_signed_pre_keys")
public class SignedPreKeyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "key_id", nullable = false)
    private int keyId;

    @Column(name = "public_key", nullable = false)
    private byte[] publicKey;

    @Column(nullable = false)
    private byte[] signature;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public int getKeyId() { return keyId; }
    public void setKeyId(int keyId) { this.keyId = keyId; }

    public byte[] getPublicKey() { return publicKey; }
    public void setPublicKey(byte[] publicKey) { this.publicKey = publicKey; }

    public byte[] getSignature() { return signature; }
    public void setSignature(byte[] signature) { this.signature = signature; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}
