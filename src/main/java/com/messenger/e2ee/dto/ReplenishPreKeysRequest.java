package com.messenger.e2ee.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record ReplenishPreKeysRequest(
        @NotNull List<RegisterKeysRequest.PreKeyData> preKeys
) {}
