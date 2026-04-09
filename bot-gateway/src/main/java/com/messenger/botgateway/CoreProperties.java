package com.messenger.botgateway;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "core")
public record CoreProperties(
        String baseUrl,
        String internalBotGatewayKey
) {
    public String normalizedBase() {
        if (baseUrl == null || baseUrl.isBlank()) {
            return "http://localhost:3000";
        }
        return baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }
}
