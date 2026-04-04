package com.messenger.e2ee.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record RegisterKeysRequest(
        @NotNull Integer registrationId,
        @NotNull String identityPublicKey,
        @NotNull SignedPreKeyData signedPreKey,
        @NotNull List<PreKeyData> preKeys
) {
    public record SignedPreKeyData(
            @NotNull Integer keyId,
            @NotNull String publicKey,
            @NotNull String signature
    ) {}

    public record PreKeyData(
            @NotNull Integer keyId,
            @NotNull String publicKey
    ) {}
}
