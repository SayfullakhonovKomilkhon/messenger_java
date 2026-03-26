package com.messenger.chat.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record GroupMemberRequest(
        @NotNull(message = "userId is required") UUID userId
) {}
