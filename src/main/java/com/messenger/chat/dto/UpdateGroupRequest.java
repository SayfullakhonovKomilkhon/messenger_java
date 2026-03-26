package com.messenger.chat.dto;

import jakarta.validation.constraints.Size;

public record UpdateGroupRequest(
        @Size(min = 1, max = 100, message = "Title must be 1-100 characters")
        String title,

        @Size(max = 500, message = "Description must be at most 500 characters")
        String description,

        String avatarUrl
) {}
