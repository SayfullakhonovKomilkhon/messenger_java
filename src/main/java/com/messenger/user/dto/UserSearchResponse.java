package com.messenger.user.dto;

public record UserSearchResponse(
        String id,
        String publicId,
        String name,
        String username,
        String avatarUrl,
        Boolean isOnline,
        Boolean isBot,
        String matchType
) {}
