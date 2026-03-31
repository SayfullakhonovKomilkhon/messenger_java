package com.messenger.e2ee.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ConsumedRequest(
        @NotNull UUID groupId,
        @NotNull UUID senderId
) {}
