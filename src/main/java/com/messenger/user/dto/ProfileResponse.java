package com.messenger.user.dto;

public record ProfileResponse(
        String id,
        String publicId,
        String name,
        String phone,
        String username,
        String aiName,
        String avatarUrl,
        String bio,
        Boolean isOnline,
        Boolean isBot,
        String lastSeenAt
) {}
