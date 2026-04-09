package com.messenger.bot;

import com.messenger.bot.dto.BotResponse;
import com.messenger.bot.dto.CreateBotRequest;
import com.messenger.bot.dto.UpdateBotRequest;
import com.messenger.bot.entity.Bot;
import com.messenger.common.exception.AppException;
import com.messenger.user.BotUserService;
import com.messenger.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class BotService {

    private static final Logger log = LoggerFactory.getLogger(BotService.class);
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final BotRepository botRepository;
    private final BotUserService botUserService;
    private final UserRepository userRepository;

    public BotService(BotRepository botRepository, BotUserService botUserService, UserRepository userRepository) {
        this.botRepository = botRepository;
        this.botUserService = botUserService;
        this.userRepository = userRepository;
    }

    @Transactional
    public BotResponse createBot(UUID ownerId, CreateBotRequest request) {
        if (!botUserService.ownerExists(ownerId)) {
            throw new AppException("Owner not found", HttpStatus.NOT_FOUND);
        }

        String username = normalizeUsername(request.username());
        if (username != null) {
            if (botRepository.existsByUsername(username) || userRepository.existsByUsername(username)) {
                throw new AppException("Bot username already taken", HttpStatus.CONFLICT);
            }
        }

        UUID botUserId = botUserService.createBotUser(
                request.name(), username, request.avatarUrl(), request.description());

        String token = generateToken();

        Bot bot = new Bot();
        bot.setUserId(botUserId);
        bot.setOwnerId(ownerId);
        bot.setName(request.name());
        bot.setUsername(username);
        bot.setDescription(request.description());
        bot.setAvatarUrl(request.avatarUrl());
        bot.setToken(token);
        bot = botRepository.save(bot);

        log.info("Bot '{}' created by user {}, botUserId={}", request.name(), ownerId, botUserId);
        return toResponse(bot);
    }

    public List<BotResponse> getMyBots(UUID ownerId) {
        return botRepository.findByOwnerId(ownerId).stream()
                .map(this::toResponse)
                .toList();
    }

    public BotResponse getBot(UUID botId, UUID ownerId) {
        Bot bot = botRepository.findById(botId)
                .orElseThrow(() -> new AppException("Bot not found", HttpStatus.NOT_FOUND));
        if (!bot.getOwnerId().equals(ownerId)) {
            throw new AppException("Not your bot", HttpStatus.FORBIDDEN);
        }
        return toResponse(bot);
    }

    @Transactional
    public BotResponse updateBot(UUID botId, UUID ownerId, UpdateBotRequest request) {
        Bot bot = botRepository.findById(botId)
                .orElseThrow(() -> new AppException("Bot not found", HttpStatus.NOT_FOUND));
        if (!bot.getOwnerId().equals(ownerId)) {
            throw new AppException("Not your bot", HttpStatus.FORBIDDEN);
        }

        if (request.name() != null) bot.setName(request.name());
        if (request.username() != null) {
            String newUsername = normalizeUsername(request.username());
            if (!Objects.equals(newUsername, bot.getUsername())) {
                if (newUsername != null
                        && (botRepository.existsByUsername(newUsername) || userRepository.existsByUsername(newUsername))) {
                    throw new AppException("Bot username already taken", HttpStatus.CONFLICT);
                }
            }
            bot.setUsername(newUsername);
        }
        if (request.description() != null) bot.setDescription(request.description());
        if (request.avatarUrl() != null) bot.setAvatarUrl(request.avatarUrl());
        if (request.webhookUrl() != null) bot.setWebhookUrl(request.webhookUrl());

        // Display name lives on Bot; users.name for bot rows stays internal (unique).
        botUserService.updateBotUser(bot.getUserId(),
                null, request.username(), request.description(), request.avatarUrl());

        bot = botRepository.save(bot);
        return toResponse(bot);
    }

    @Transactional
    public BotResponse regenerateToken(UUID botId, UUID ownerId) {
        Bot bot = botRepository.findById(botId)
                .orElseThrow(() -> new AppException("Bot not found", HttpStatus.NOT_FOUND));
        if (!bot.getOwnerId().equals(ownerId)) {
            throw new AppException("Not your bot", HttpStatus.FORBIDDEN);
        }

        bot.setToken(generateToken());
        bot = botRepository.save(bot);
        log.info("Token regenerated for bot {}", botId);
        return toResponse(bot);
    }

    @Transactional
    public BotResponse toggleActive(UUID botId, UUID ownerId) {
        Bot bot = botRepository.findById(botId)
                .orElseThrow(() -> new AppException("Bot not found", HttpStatus.NOT_FOUND));
        if (!bot.getOwnerId().equals(ownerId)) {
            throw new AppException("Not your bot", HttpStatus.FORBIDDEN);
        }
        bot.setIsActive(!Boolean.TRUE.equals(bot.getIsActive()));
        bot = botRepository.save(bot);
        log.info("Bot {} active status toggled to {} by owner {}", botId, bot.getIsActive(), ownerId);
        return toResponse(bot);
    }

    @Transactional
    public void deleteBot(UUID botId, UUID ownerId) {
        Bot bot = botRepository.findById(botId)
                .orElseThrow(() -> new AppException("Bot not found", HttpStatus.NOT_FOUND));
        if (!bot.getOwnerId().equals(ownerId)) {
            throw new AppException("Not your bot", HttpStatus.FORBIDDEN);
        }

        botUserService.deleteBotUser(bot.getUserId());
        botRepository.delete(bot);
        log.info("Bot {} deleted by owner {}", botId, ownerId);
    }

    public Bot findByToken(String token) {
        return botRepository.findByToken(token)
                .orElseThrow(() -> new AppException("Invalid bot token", HttpStatus.UNAUTHORIZED));
    }

    public Bot findByUserId(UUID userId) {
        return botRepository.findByUserId(userId).orElse(null);
    }

    private static String normalizeUsername(String username) {
        if (username == null) return null;
        String t = username.trim();
        return t.isEmpty() ? null : t;
    }

    private String generateToken() {
        byte[] bytes = new byte[48];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private BotResponse toResponse(Bot bot) {
        return new BotResponse(
                bot.getId().toString(),
                bot.getUserId().toString(),
                bot.getName(),
                bot.getUsername(),
                bot.getDescription(),
                bot.getAvatarUrl(),
                bot.getToken(),
                bot.getWebhookUrl(),
                Boolean.TRUE.equals(bot.getIsActive()),
                bot.getCreatedAt()
        );
    }
}
