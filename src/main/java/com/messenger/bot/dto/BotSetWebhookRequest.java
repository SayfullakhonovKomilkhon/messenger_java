package com.messenger.bot.dto;

import jakarta.validation.constraints.Size;

public record BotSetWebhookRequest(
        @Size(max = 500, message = "Webhook URL too long")
        String url
) {}
