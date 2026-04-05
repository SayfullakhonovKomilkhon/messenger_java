package com.messenger.user;

import com.messenger.chat.ParticipantRepository;
import com.messenger.chat.entity.ConversationParticipant;
import com.messenger.common.exception.AppException;
import com.messenger.user.dto.ProfileResponse;
import com.messenger.user.dto.UpdateProfileRequest;
import com.messenger.user.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ProfileService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ParticipantRepository participantRepository;

    public ProfileService(UserRepository userRepository, UserMapper userMapper,
                          ParticipantRepository participantRepository) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.participantRepository = participantRepository;
    }

    public ProfileResponse getProfile(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));
        return userMapper.toProfileResponse(user);
    }

    public ProfileResponse getPublicProfile(UUID viewerId, UUID targetId) {
        User user = userRepository.findById(targetId)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));

        if (viewerId.equals(targetId)) {
            return userMapper.toProfileResponse(user);
        }

        List<ConversationParticipant> participants =
                participantRepository.findDirectConversationParticipants(viewerId, targetId);

        boolean mutualTrust = false;
        String searchMethod = null;

        if (participants.size() >= 2) {
            ConversationParticipant viewerPart = null;
            ConversationParticipant targetPart = null;
            for (ConversationParticipant p : participants) {
                if (p.getUser().getId().equals(viewerId)) viewerPart = p;
                else if (p.getUser().getId().equals(targetId)) targetPart = p;
            }
            if (viewerPart != null && targetPart != null) {
                mutualTrust = "TRUSTED".equals(viewerPart.getTrustStatus())
                        && "TRUSTED".equals(targetPart.getTrustStatus());
                searchMethod = viewerPart.getSearchMethod();
            }
        }

        if (mutualTrust) {
            return userMapper.toProfileResponse(user);
        }

        return userMapper.toLimitedProfileResponse(user, searchMethod);
    }

    @Transactional
    public ProfileResponse updateProfile(UUID userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));

        if (request.name() != null && !request.name().isBlank()) {
            user.setName(request.name());
        }
        if (request.username() != null) {
            if (!request.username().isBlank()) {
                userRepository.findByUsername(request.username()).ifPresent(existing -> {
                    if (!existing.getId().equals(userId)) {
                        throw new AppException("Username already taken", HttpStatus.CONFLICT);
                    }
                });
            }
            user.setUsername(request.username().isBlank() ? null : request.username());
        }
        if (request.aiName() != null) {
            user.setAiName(request.aiName().isBlank() ? null : request.aiName());
        }
        if (request.bio() != null) {
            user.setBio(request.bio());
        }
        if (request.avatarUrl() != null) {
            user.setAvatarUrl(request.avatarUrl());
        }

        userRepository.save(user);
        return userMapper.toProfileResponse(user);
    }

    @Transactional
    public void deleteAccount(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new AppException("User not found", HttpStatus.NOT_FOUND);
        }
        userRepository.deleteById(userId);
    }
}
