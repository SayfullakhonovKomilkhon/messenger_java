package com.messenger.chat.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ConversationResponse(
        String id,
        String type,
        LocalDateTime updatedAt,
        ParticipantInfo participant,
        GroupInfo groupInfo,
        LastMessageInfo lastMessage,
        int unreadCount,
        boolean isPinned,
        boolean isMuted,
        String myTrustStatus,
        String otherTrustStatus,
        String searchMethod
) {
    public ConversationResponse(
            String id,
            LocalDateTime updatedAt,
            ParticipantInfo participant,
            LastMessageInfo lastMessage,
            int unreadCount,
            boolean isPinned,
            boolean isMuted,
            String myTrustStatus,
            String otherTrustStatus,
            String searchMethod
    ) {
        this(id, "DIRECT", updatedAt, participant, null, lastMessage,
                unreadCount, isPinned, isMuted, myTrustStatus, otherTrustStatus, searchMethod);
    }

    public record ParticipantInfo(
            String id,
            String publicId,
            String name,
            String aiName,
            String avatarUrl,
            Boolean isOnline,
            Boolean isBot
    ) {}

    public record GroupInfo(
            String title,
            String description,
            String avatarUrl,
            int memberCount,
            String myRole,
            String createdBy,
            List<GroupMemberInfo> members
    ) {}

    public record GroupMemberInfo(
            String userId,
            String name,
            String avatarUrl,
            Boolean isOnline,
            String role,
            LocalDateTime joinedAt
    ) {}

    public record LastMessageInfo(
            String id,
            String text,
            LocalDateTime createdAt,
            String status,
            String fileUrl,
            String mimeType,
            Boolean isVoiceMessage,
            Boolean encrypted
    ) {}
}
