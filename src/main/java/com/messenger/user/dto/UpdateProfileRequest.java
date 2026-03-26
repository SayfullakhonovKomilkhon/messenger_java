package com.messenger.user.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @Size(min = 1, max = 100, message = "Имя должно быть от 1 до 100 символов")
        String name,

        @Size(max = 50, message = "Username не более 50 символов")
        @Pattern(regexp = "^[a-zA-Z0-9._-]*$", message = "Username может содержать только буквы, цифры, точку, дефис и подчёркивание")
        String username,

        @Size(max = 500, message = "Био не более 500 символов")
        String bio,

        @Size(max = 500, message = "URL аватара не более 500 символов")
        String avatarUrl
) {}
