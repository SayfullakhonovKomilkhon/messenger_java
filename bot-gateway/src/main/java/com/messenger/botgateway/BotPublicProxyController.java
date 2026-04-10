package com.messenger.botgateway;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

/**
 * Public Bot API — forwards to messenger-core /internal/v1/bot/* with shared secret.
 */
@RestController
@RequestMapping("/api/v1/bot")
@CrossOrigin(origins = "*")
public class BotPublicProxyController {

    private static final Logger log = LoggerFactory.getLogger(BotPublicProxyController.class);

    private final RestTemplate restTemplate;
    private final CoreProperties core;

    public BotPublicProxyController(RestTemplate coreRestTemplate, CoreProperties core) {
        this.restTemplate = coreRestTemplate;
        this.core = core;
    }

    @PostConstruct
    void validateConfig() {
        if (core.internalBotGatewayKey() == null || core.internalBotGatewayKey().isBlank()) {
            log.warn("INTERNAL_BOT_GATEWAY_KEY is empty — core will reject internal calls until both apps share the same key");
        }
    }

    private String coreBotPath(String subPath) {
        return core.normalizedBase() + "/internal/v1/bot" + subPath;
    }

    private HttpHeaders forwardHeaders(String authorization) {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        if (authorization != null) {
            h.set("Authorization", authorization);
        }
        h.set("X-Internal-Bot-Key", core.internalBotGatewayKey() != null ? core.internalBotGatewayKey() : "");
        return h;
    }

    private ResponseEntity<String> exchange(HttpMethod method, String url, HttpEntity<String> entity) {
        try {
            ResponseEntity<String> r = restTemplate.exchange(url, method, entity, String.class);
            return ResponseEntity.status(r.getStatusCode()).body(r.getBody());
        } catch (HttpStatusCodeException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAsString());
        } catch (RestClientException ex) {
            log.error("Core request failed (check CORE_BASE_URL and that backend is up): {} — {}", url, ex.toString());
            return ResponseEntity.status(502)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"error\":\"BAD_GATEWAY\",\"message\":\"Cannot reach messenger core from bot-gateway\"}");
        }
    }

    @PostMapping("/sendMessage")
    public ResponseEntity<String> sendMessage(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody(required = false) String body) {
        String url = coreBotPath("/sendMessage");
        return exchange(HttpMethod.POST, url, new HttpEntity<>(body, forwardHeaders(authorization)));
    }

    @GetMapping("/getConversations")
    public ResponseEntity<String> getConversations(
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        String url = coreBotPath("/getConversations");
        return exchange(HttpMethod.GET, url, new HttpEntity<>(forwardHeaders(authorization)));
    }

    @GetMapping("/getMessages")
    public ResponseEntity<String> getMessages(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestParam UUID conversationId,
            @RequestParam(required = false) UUID before,
            @RequestParam(defaultValue = "30") int limit) {
        UriComponentsBuilder b = UriComponentsBuilder.fromHttpUrl(coreBotPath("/getMessages"))
                .queryParam("conversationId", conversationId)
                .queryParam("limit", limit);
        if (before != null) {
            b.queryParam("before", before);
        }
        return exchange(HttpMethod.GET, b.toUriString(), new HttpEntity<>(forwardHeaders(authorization)));
    }

    @PostMapping("/startConversation")
    public ResponseEntity<String> startConversation(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestParam UUID userId) {
        String url = UriComponentsBuilder.fromHttpUrl(coreBotPath("/startConversation"))
                .queryParam("userId", userId)
                .toUriString();
        return exchange(HttpMethod.POST, url, new HttpEntity<>(forwardHeaders(authorization)));
    }

    @PostMapping("/setWebhook")
    public ResponseEntity<String> setWebhook(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody(required = false) String body) {
        String url = coreBotPath("/setWebhook");
        return exchange(HttpMethod.POST, url, new HttpEntity<>(body, forwardHeaders(authorization)));
    }

    @DeleteMapping("/webhook")
    public ResponseEntity<String> deleteWebhook(
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        String url = coreBotPath("/webhook");
        HttpHeaders h = forwardHeaders(authorization);
        h.remove(HttpHeaders.CONTENT_TYPE);
        return exchange(HttpMethod.DELETE, url, new HttpEntity<>(h));
    }

    @GetMapping("/me")
    public ResponseEntity<String> me(
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        String url = coreBotPath("/me");
        return exchange(HttpMethod.GET, url, new HttpEntity<>(forwardHeaders(authorization)));
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("{\"service\":\"bot-gateway\"}");
    }
}
