package com.messenger.bot;

import com.messenger.bot.dto.BotSendMessageRequest;
import com.messenger.bot.dto.BotSetWebhookRequest;
import com.messenger.bot.entity.Bot;
import com.messenger.chat.dto.ConversationResponse;
import com.messenger.chat.dto.MessageResponse;
import com.messenger.common.exception.AppException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Bot-facing API. Authenticated via "Authorization: Bot <token>" header.
 * No JWT, no user session — bots use their own token.
 */
@RestController
@RequestMapping("/api/v1/bot")
public class BotApiController {

    private final BotService botService;
    private final BotMessagingFacade messagingFacade;
    private final BotRepository botRepository;

    public BotApiController(BotService botService, BotMessagingFacade messagingFacade, BotRepository botRepository) {
        this.botService = botService;
        this.messagingFacade = messagingFacade;
        this.botRepository = botRepository;
    }

    @PostMapping("/sendMessage")
    public ResponseEntity<MessageResponse> sendMessage(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody BotSendMessageRequest request) {
        Bot bot = resolveBot(authHeader);
        return ResponseEntity.ok(messagingFacade.sendMessage(bot, request));
    }

    @GetMapping("/getConversations")
    public ResponseEntity<List<ConversationResponse>> getConversations(
            @RequestHeader("Authorization") String authHeader) {
        Bot bot = resolveBot(authHeader);
        return ResponseEntity.ok(messagingFacade.getConversations(bot));
    }

    @GetMapping("/getMessages")
    public ResponseEntity<List<MessageResponse>> getMessages(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam UUID conversationId,
            @RequestParam(required = false) UUID before,
            @RequestParam(defaultValue = "30") int limit) {
        Bot bot = resolveBot(authHeader);
        return ResponseEntity.ok(messagingFacade.getMessages(bot, conversationId, before, limit));
    }

    @PostMapping("/startConversation")
    public ResponseEntity<ConversationResponse> startConversation(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam UUID userId) {
        Bot bot = resolveBot(authHeader);
        return ResponseEntity.ok(messagingFacade.startConversation(bot, userId));
    }

    @PostMapping("/setWebhook")
    public ResponseEntity<Map<String, String>> setWebhook(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody BotSetWebhookRequest request) {
        Bot bot = resolveBot(authHeader);
        bot.setWebhookUrl(request.url());
        botRepository.save(bot);
        return ResponseEntity.ok(Map.of("status", "ok", "webhook_url", request.url() != null ? request.url() : ""));
    }

    @DeleteMapping("/webhook")
    public ResponseEntity<Map<String, String>> deleteWebhook(
            @RequestHeader("Authorization") String authHeader) {
        Bot bot = resolveBot(authHeader);
        bot.setWebhookUrl(null);
        botRepository.save(bot);
        return ResponseEntity.ok(Map.of("status", "ok", "webhook_url", ""));
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getMe(
            @RequestHeader("Authorization") String authHeader) {
        Bot bot = resolveBot(authHeader);
        return ResponseEntity.ok(Map.of(
                "id", bot.getId().toString(),
                "user_id", bot.getUserId().toString(),
                "name", bot.getName(),
                "username", bot.getUsername() != null ? bot.getUsername() : "",
                "is_active", Boolean.TRUE.equals(bot.getIsActive())
        ));
    }

    private Bot resolveBot(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bot ")) {
            throw new AppException("Authorization header must be: Bot <token>", HttpStatus.UNAUTHORIZED);
        }
        String token = authHeader.substring(4).trim();
        return botService.findByToken(token);
    }
}
