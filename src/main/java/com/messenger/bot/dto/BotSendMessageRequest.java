package com.messenger.bot.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record BotSendMessageRequest(
        @NotNull(message = "conversationId is required")
        UUID conversationId,

        String text,

        String fileUrl,

        String mimeType
) {}
