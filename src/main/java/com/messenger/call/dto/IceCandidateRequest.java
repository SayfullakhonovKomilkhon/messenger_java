package com.messenger.call.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record IceCandidateRequest(
        @NotNull UUID callId,
        @NotBlank String candidate,
        Boolean encrypted
) {
    public boolean isEncrypted() {
        return encrypted != null && encrypted;
    }
}
