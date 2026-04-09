package com.messenger.user;

import com.messenger.bot.BotRepository;
import com.messenger.bot.entity.Bot;
import com.messenger.user.dto.BlockedUserResponse;
import com.messenger.user.dto.ProfileResponse;
import com.messenger.user.dto.UserSearchResponse;
import com.messenger.user.entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class UserMapper {

    private final BotRepository botRepository;

    public UserMapper(BotRepository botRepository) {
        this.botRepository = botRepository;
    }

    public ProfileResponse toProfileResponse(User user) {
        Optional<Bot> bot = linkedBot(user);
        return new ProfileResponse(
                user.getId().toString(),
                user.getPublicId(),
                bot.map(Bot::getName).orElse(user.getName()),
                user.getPhone(),
                bot.map(Bot::getUsername).orElse(user.getUsername()),
                user.getAiName(),
                bot.map(Bot::getAvatarUrl).orElse(user.getAvatarUrl()),
                user.getBio(),
                user.getIsOnline(),
                user.getIsBot(),
                user.getLastSeenAt() != null ? user.getLastSeenAt().toString() : null
        );
    }

    public ProfileResponse toLimitedProfileResponse(User user, String searchMethod) {
        String visiblePublicId = null;
        String visibleAiName = null;

        if ("publicId".equals(searchMethod)) {
            visiblePublicId = user.getPublicId();
        } else if ("aiName".equals(searchMethod)) {
            visibleAiName = user.getAiName();
        } else {
            visiblePublicId = user.getPublicId();
        }

        return new ProfileResponse(
                user.getId().toString(),
                visiblePublicId,
                null,
                null,
                null,
                visibleAiName,
                null,
                null,
                user.getIsOnline(),
                user.getIsBot(),
                user.getLastSeenAt() != null ? user.getLastSeenAt().toString() : null
        );
    }

    public UserSearchResponse toSearchResponse(User user) {
        Optional<Bot> bot = linkedBot(user);
        return new UserSearchResponse(
                user.getId().toString(),
                user.getPublicId(),
                bot.map(Bot::getName).orElse(user.getName()),
                user.getAiName(),
                user.getIsOnline(),
                user.getIsBot(),
                null
        );
    }

    public BlockedUserResponse toBlockedResponse(User user, LocalDateTime blockedAt) {
        Optional<Bot> bot = linkedBot(user);
        return new BlockedUserResponse(
                user.getId().toString(),
                bot.map(Bot::getName).orElse(user.getName()),
                bot.map(Bot::getAvatarUrl).orElse(user.getAvatarUrl()),
                blockedAt
        );
    }

    private Optional<Bot> linkedBot(User user) {
        if (!Boolean.TRUE.equals(user.getIsBot())) {
            return Optional.empty();
        }
        return botRepository.findByUserId(user.getId());
    }
}
