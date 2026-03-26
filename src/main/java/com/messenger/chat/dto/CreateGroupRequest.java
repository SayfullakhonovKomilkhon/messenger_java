package com.messenger.chat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record CreateGroupRequest(
        @NotBlank(message = "Group title is required")
        @Size(min = 1, max = 100, message = "Title must be 1-100 characters")
        String title,

        @Size(max = 500, message = "Description must be at most 500 characters")
        String description,

        String avatarUrl,

        List<UUID> memberIds
) {}
