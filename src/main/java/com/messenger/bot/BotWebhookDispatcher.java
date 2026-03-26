package com.messenger.bot;

import com.messenger.bot.entity.Bot;
import com.messenger.chat.dto.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Dispatches incoming messages to bot webhook URLs.
 * Runs async to not block the main message flow.
 */
@Component
public class BotWebhookDispatcher {

    private static final Logger log = LoggerFactory.getLogger(BotWebhookDispatcher.class);

    private final RestTemplate restTemplate;

    public BotWebhookDispatcher() {
        this.restTemplate = new RestTemplate();
    }

    @Async
    public void dispatch(Bot bot, MessageResponse message) {
        String webhookUrl = bot.getWebhookUrl();
        if (webhookUrl == null || webhookUrl.isBlank()) {
            return;
        }

        try {
            Map<String, Object> msgMap = new LinkedHashMap<>();
            msgMap.put("id", message.id() != null ? message.id() : "");
            msgMap.put("conversationId", message.conversationId());
            msgMap.put("senderId", message.senderId());
            msgMap.put("text", message.text() != null ? message.text() : "");
            msgMap.put("fileUrl", message.fileUrl() != null ? message.fileUrl() : "");
            msgMap.put("mimeType", message.mimeType() != null ? message.mimeType() : "");
            msgMap.put("createdAt", message.createdAt() != null ? message.createdAt().toString() : "");

            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("update_type", "message");
            payload.put("bot_id", bot.getId().toString());
            payload.put("message", msgMap);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            restTemplate.postForEntity(webhookUrl, new HttpEntity<>(payload, headers), Void.class);
            log.debug("Webhook delivered to bot {} at {}", bot.getId(), webhookUrl);
        } catch (Exception e) {
            log.warn("Webhook failed for bot {} at {}: {}", bot.getId(), webhookUrl, e.getMessage());
        }
    }
}
