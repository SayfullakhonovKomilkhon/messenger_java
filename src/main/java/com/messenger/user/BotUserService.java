package com.messenger.user;

import com.messenger.common.exception.AppException;
import com.messenger.user.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Narrow facade for bot-related user operations.
 * Bot module depends on this instead of UserRepository directly.
 */
@Service
public class BotUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public BotUserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean ownerExists(UUID ownerId) {
        return userRepository.existsById(ownerId);
    }

    @Transactional
    public UUID createBotUser(String name, String username, String avatarUrl, String description) {
        User botUser = new User();
        botUser.setName(name);
        botUser.setUsername(username);
        botUser.setAvatarUrl(avatarUrl);
        botUser.setIsBot(true);
        botUser.setPhone("bot_" + UUID.randomUUID().toString().substring(0, 12));
        botUser.setPasswordHash(passwordEncoder.encode(UUID.randomUUID().toString()));
        botUser.setBio(description);
        botUser.setIsOnline(true);
        botUser = userRepository.save(botUser);
        return botUser.getId();
    }

    @Transactional
    public void updateBotUser(UUID botUserId, String name, String username, String bio, String avatarUrl) {
        User botUser = userRepository.findById(botUserId)
                .orElseThrow(() -> new AppException("Bot user not found", HttpStatus.NOT_FOUND));
        if (name != null) botUser.setName(name);
        if (username != null) botUser.setUsername(username);
        if (bio != null) botUser.setBio(bio);
        if (avatarUrl != null) botUser.setAvatarUrl(avatarUrl);
        userRepository.save(botUser);
    }
}
