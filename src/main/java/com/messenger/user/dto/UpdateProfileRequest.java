package com.messenger.user.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @Size(min = 1, max = 100, message = "Ник должен быть от 1 до 100 символов")
        @Pattern(regexp = "^[а-яА-ЯёЁ\\s-]*$", message = "Ник может содержать только русские буквы, пробел и дефис")
        String name,

        @Size(max = 50, message = "Имя пользователя не более 50 символов")
        @Pattern(regexp = "^[a-zA-Z0-9._-]*$", message = "Имя пользователя может содержать только буквы, цифры, точку, дефис и подчёркивание")
        String username,

        @Size(max = 100, message = "Имя аватара не более 100 символов")
        @Pattern(regexp = "^[а-яА-ЯёЁ\\s-]*$", message = "Имя аватара может содержать только русские буквы, пробел и дефис")
        String aiName,

        @Size(max = 70, message = "Био не более 70 символов")
        String bio,

        @Size(max = 500, message = "URL аватара не более 500 символов")
        String avatarUrl
) {}
