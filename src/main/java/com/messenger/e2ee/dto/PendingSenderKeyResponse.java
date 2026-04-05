package com.messenger.e2ee.dto;

import java.util.UUID;

public record PendingSenderKeyResponse(
        UUID groupId,
        UUID senderId,
        String encryptedSkdm
) {}
