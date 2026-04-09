package com.messenger.user.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @Size(min = 1, max = 100, message = "{nick.size}")
        @Pattern(regexp = "^[а-яА-ЯёЁ\\s-]*$", message = "{nick.pattern}")
        String name,

        @Size(max = 50, message = "{username.size}")
        @Pattern(regexp = "^[a-zA-Z0-9._-]*$", message = "{username.pattern}")
        String username,

        @Size(max = 100, message = "{ainame.size}")
        @Pattern(regexp = "^[а-яА-ЯёЁ\\s-]*$", message = "{ainame.pattern}")
        String aiName,

        @Size(max = 70, message = "{bio.size}")
        String bio,

        @Size(max = 500, message = "{avatar.size}")
        String avatarUrl
) {}
