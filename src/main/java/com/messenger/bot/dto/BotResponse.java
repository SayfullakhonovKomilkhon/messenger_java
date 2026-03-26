package com.messenger.bot.dto;

import java.time.LocalDateTime;

public record BotResponse(
        String id,
        String userId,
        String name,
        String username,
        String description,
        String avatarUrl,
        String token,
        String webhookUrl,
        boolean isActive,
        LocalDateTime createdAt
) {}
