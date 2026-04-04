package com.messenger.auth.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        UserInfo user
) {
    public record UserInfo(
            String id,
            String publicId,
            String name,
            String phone,
            String avatarUrl
    ) {}
}
