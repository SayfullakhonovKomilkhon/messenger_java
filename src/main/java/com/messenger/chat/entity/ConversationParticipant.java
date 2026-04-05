package com.messenger.chat.entity;

import com.messenger.user.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "conversation_participants",
        uniqueConstraints = @UniqueConstraint(columnNames = {"conversation_id", "user_id"}))
public class ConversationParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "unread_count")
    private Integer unreadCount = 0;

    @Column(name = "last_read_at")
    private LocalDateTime lastReadAt;

    @Column(name = "is_pinned")
    private Boolean isPinned = false;

    @Column(name = "is_muted")
    private Boolean isMuted = false;

    @Column(name = "is_notifications_enabled")
    private Boolean isNotificationsEnabled = true;

    @Column(name = "status", length = 20)
    private String status = "ACTIVE";

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 10)
    private GroupRole role = GroupRole.MEMBER;

    @Column(name = "trust_status", length = 10)
    private String trustStatus = "PENDING";

    @Column(name = "search_method", length = 10)
    private String searchMethod;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    @PrePersist
    protected void onJoin() {
        if (joinedAt == null) joinedAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Conversation getConversation() { return conversation; }
    public void setConversation(Conversation conversation) { this.conversation = conversation; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Integer getUnreadCount() { return unreadCount; }
    public void setUnreadCount(Integer unreadCount) { this.unreadCount = unreadCount; }

    public LocalDateTime getLastReadAt() { return lastReadAt; }
    public void setLastReadAt(LocalDateTime lastReadAt) { this.lastReadAt = lastReadAt; }

    public Boolean getIsPinned() { return isPinned; }
    public void setIsPinned(Boolean isPinned) { this.isPinned = isPinned; }

    public Boolean getIsMuted() { return isMuted; }
    public void setIsMuted(Boolean isMuted) { this.isMuted = isMuted; }

    public Boolean getIsNotificationsEnabled() { return isNotificationsEnabled; }
    public void setIsNotificationsEnabled(Boolean isNotificationsEnabled) { this.isNotificationsEnabled = isNotificationsEnabled; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public GroupRole getRole() { return role; }
    public void setRole(GroupRole role) { this.role = role; }

    public String getTrustStatus() { return trustStatus; }
    public void setTrustStatus(String trustStatus) { this.trustStatus = trustStatus; }

    public String getSearchMethod() { return searchMethod; }
    public void setSearchMethod(String searchMethod) { this.searchMethod = searchMethod; }

    public LocalDateTime getJoinedAt() { return joinedAt; }
    public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }
}
