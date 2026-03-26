package com.messenger.chat.dto;

import com.messenger.chat.entity.GroupRole;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ChangeRoleRequest(
        @NotNull UUID userId,
        @NotNull GroupRole role
) {}
