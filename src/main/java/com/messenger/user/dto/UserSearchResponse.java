package com.messenger.user.dto;

public record UserSearchResponse(
        String id,
        String publicId,
        String aiName,
        Boolean isOnline,
        Boolean isBot,
        String matchType
) {}
