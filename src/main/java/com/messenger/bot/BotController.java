package com.messenger.bot;

import com.messenger.bot.dto.BotResponse;
import com.messenger.bot.dto.CreateBotRequest;
import com.messenger.bot.dto.UpdateBotRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * User-facing bot management. Authenticated via JWT (standard user auth).
 */
@RestController
@RequestMapping("/api/v1/bots")
public class BotController {

    private final BotService botService;

    public BotController(BotService botService) {
        this.botService = botService;
    }

    @PostMapping
    public ResponseEntity<BotResponse> createBot(
            Authentication auth,
            @Valid @RequestBody CreateBotRequest request) {
        UUID userId = UUID.fromString((String) auth.getPrincipal());
        return ResponseEntity.status(HttpStatus.CREATED).body(botService.createBot(userId, request));
    }

    @GetMapping
    public ResponseEntity<List<BotResponse>> getMyBots(Authentication auth) {
        UUID userId = UUID.fromString((String) auth.getPrincipal());
        return ResponseEntity.ok(botService.getMyBots(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BotResponse> getBot(Authentication auth, @PathVariable UUID id) {
        UUID userId = UUID.fromString((String) auth.getPrincipal());
        return ResponseEntity.ok(botService.getBot(id, userId));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BotResponse> updateBot(
            Authentication auth,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateBotRequest request) {
        UUID userId = UUID.fromString((String) auth.getPrincipal());
        return ResponseEntity.ok(botService.updateBot(id, userId, request));
    }

    @PostMapping("/{id}/regenerate-token")
    public ResponseEntity<BotResponse> regenerateToken(Authentication auth, @PathVariable UUID id) {
        UUID userId = UUID.fromString((String) auth.getPrincipal());
        return ResponseEntity.ok(botService.regenerateToken(id, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBot(Authentication auth, @PathVariable UUID id) {
        UUID userId = UUID.fromString((String) auth.getPrincipal());
        botService.deleteBot(id, userId);
        return ResponseEntity.noContent().build();
    }
}
