package com.messenger.user;

import com.messenger.user.dto.BlockedUserResponse;
import com.messenger.user.dto.ProfileResponse;
import com.messenger.user.dto.UserSearchResponse;
import com.messenger.user.entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UserMapper {

    public ProfileResponse toProfileResponse(User user) {
        return new ProfileResponse(
                user.getId().toString(),
                user.getPublicId(),
                user.getName(),
                user.getPhone(),
                user.getUsername(),
                user.getAiName(),
                user.getAvatarUrl(),
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
        return new UserSearchResponse(
                user.getId().toString(),
                user.getPublicId(),
                user.getAiName(),
                user.getIsOnline(),
                user.getIsBot(),
                null
        );
    }

    public BlockedUserResponse toBlockedResponse(User user, LocalDateTime blockedAt) {
        return new BlockedUserResponse(
                user.getId().toString(),
                user.getName(),
                user.getAvatarUrl(),
                blockedAt
        );
    }
}
