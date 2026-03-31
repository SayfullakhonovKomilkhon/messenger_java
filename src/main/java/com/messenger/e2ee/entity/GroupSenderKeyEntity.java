package com.messenger.e2ee.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "group_sender_keys",
        uniqueConstraints = @UniqueConstraint(columnNames = {"group_id", "sender_id", "recipient_id"}))
public class GroupSenderKeyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "group_id", nullable = false)
    private UUID groupId;

    @Column(name = "sender_id", nullable = false)
    private UUID senderId;

    @Column(name = "recipient_id", nullable = false)
    private UUID recipientId;

    @Column(name = "distribution_message", nullable = false, columnDefinition = "TEXT")
    private String distributionMessage;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "consumed")
    private Boolean consumed = false;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getGroupId() { return groupId; }
    public void setGroupId(UUID groupId) { this.groupId = groupId; }

    public UUID getSenderId() { return senderId; }
    public void setSenderId(UUID senderId) { this.senderId = senderId; }

    public UUID getRecipientId() { return recipientId; }
    public void setRecipientId(UUID recipientId) { this.recipientId = recipientId; }

    public String getDistributionMessage() { return distributionMessage; }
    public void setDistributionMessage(String distributionMessage) { this.distributionMessage = distributionMessage; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public Boolean getConsumed() { return consumed; }
    public void setConsumed(Boolean consumed) { this.consumed = consumed; }
}
