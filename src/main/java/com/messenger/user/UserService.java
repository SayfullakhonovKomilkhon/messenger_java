package com.messenger.user;

import com.messenger.common.exception.AppException;
import com.messenger.user.dto.UserSearchResponse;
import com.messenger.user.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public List<UserSearchResponse> search(String query, UUID currentUserId) {
        if (query == null || query.isBlank() || query.length() < 2) {
            throw new AppException("Search query must be at least 2 characters", HttpStatus.BAD_REQUEST);
        }

        String q = query.trim();
        if (q.startsWith("#")) q = q.substring(1).trim();
        final String searchQuery = q;
        return userRepository.searchByPublicIdOrAiName(searchQuery, currentUserId).stream()
                .map(user -> {
                    boolean matchedById = user.getPublicId() != null
                            && user.getPublicId().equalsIgnoreCase(searchQuery);
                    boolean matchedByAiName = user.getAiName() != null
                            && user.getAiName().toLowerCase().contains(searchQuery.toLowerCase());

                    if (matchedById) {
                        return new UserSearchResponse(
                                user.getId().toString(),
                                user.getPublicId(),
                                null,
                                null,
                                user.getIsOnline(),
                                user.getIsBot(),
                                "publicId"
                        );
                    } else if (matchedByAiName) {
                        return new UserSearchResponse(
                                user.getId().toString(),
                                null,
                                null,
                                user.getAiName(),
                                user.getIsOnline(),
                                user.getIsBot(),
                                "aiName"
                        );
                    } else {
                        return new UserSearchResponse(
                                user.getId().toString(),
                                null,
                                user.getName(),
                                null,
                                user.getIsOnline(),
                                user.getIsBot(),
                                "name"
                        );
                    }
                })
                .toList();
    }

    @Transactional
    public void updateFcmToken(UUID userId, String fcmToken) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));
        user.setFcmToken(fcmToken);
        userRepository.save(user);
        log.info("FCM token updated for user: {}", userId);
    }

    @Transactional
    public void setOnline(UUID userId, boolean online) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setIsOnline(online);
            if (!online) {
                user.setLastSeenAt(java.time.LocalDateTime.now());
            }
            userRepository.save(user);
        });
    }

    public User getById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));
    }
}
