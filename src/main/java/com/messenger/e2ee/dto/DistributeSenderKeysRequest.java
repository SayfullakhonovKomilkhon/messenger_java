package com.messenger.e2ee.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public record DistributeSenderKeysRequest(
        @NotNull UUID groupId,
        @NotNull List<Distribution> distributions
) {
    public record Distribution(
            @NotNull UUID recipientId,
            @NotNull String encryptedSkdm
    ) {}
}
