package com.messenger.common.security;

import com.messenger.chat.ParticipantRepository;
import com.messenger.chat.entity.ConversationParticipant;
import com.messenger.chat.entity.GroupRole;
import com.messenger.common.exception.AppException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;
import java.util.UUID;

@Component
public class GroupRoleInterceptor implements HandlerInterceptor {

    private final ParticipantRepository participantRepository;

    public GroupRoleInterceptor(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod method)) {
            return true;
        }

        RequireGroupRole annotation = method.getMethodAnnotation(RequireGroupRole.class);
        if (annotation == null) {
            return true;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new AppException("Unauthorized", HttpStatus.UNAUTHORIZED);
        }

        UUID userId;
        try {
            userId = UUID.fromString(auth.getPrincipal().toString());
        } catch (IllegalArgumentException e) {
            throw new AppException("Invalid user ID", HttpStatus.UNAUTHORIZED);
        }

        @SuppressWarnings("unchecked")
        Map<String, String> pathVars = (Map<String, String>) request.getAttribute(
                HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        String convIdStr = pathVars != null ? pathVars.get("id") : null;
        if (convIdStr == null) {
            throw new AppException("Conversation ID required", HttpStatus.BAD_REQUEST);
        }

        UUID conversationId;
        try {
            conversationId = UUID.fromString(convIdStr);
        } catch (IllegalArgumentException e) {
            throw new AppException("Invalid conversation ID format", HttpStatus.BAD_REQUEST);
        }

        ConversationParticipant participant = participantRepository
                .findByConversationIdAndUserId(conversationId, userId)
                .orElseThrow(() -> new AppException("You are not a member of this group", HttpStatus.FORBIDDEN));

        GroupRole requiredRole = annotation.value();
        GroupRole actualRole = participant.getRole();

        if (actualRole == null || !actualRole.isAtLeast(requiredRole)) {
            throw new AppException("Insufficient permissions. Required: " + requiredRole, HttpStatus.FORBIDDEN);
        }

        return true;
    }
}
