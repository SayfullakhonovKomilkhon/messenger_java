package com.messenger.bot.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateBotRequest(
        @Size(min = 1, max = 100)
        String name,

        @Size(max = 50)
        @Pattern(regexp = "^[a-zA-Z0-9._-]*$", message = "Username: letters, digits, dot, dash, underscore only")
        String username,

        @Size(max = 500)
        String description,

        String avatarUrl,

        String webhookUrl
) {}
