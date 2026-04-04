package com.messenger.chat.event;

import com.messenger.chat.dto.MessageResponse;
import org.springframework.context.ApplicationEvent;

import java.util.List;
import java.util.UUID;

public class MessageSentEvent extends ApplicationEvent {

    private final MessageResponse message;
    private final List<UUID> participantUserIds;

    public MessageSentEvent(Object source, MessageResponse message, List<UUID> participantUserIds) {
        super(source);
        this.message = message;
        this.participantUserIds = participantUserIds;
    }

    public MessageResponse getMessage() {
        return message;
    }

    public List<UUID> getParticipantUserIds() {
        return participantUserIds;
    }
}
