package com.messenger.user.dto;

public record UserSearchResponse(
        String id,
        String publicId,
        String name,
        String aiName,
        Boolean isOnline,
        Boolean isBot,
        String matchType
) {}
