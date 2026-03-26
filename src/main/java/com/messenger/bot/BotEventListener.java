package com.messenger.bot;

import com.messenger.bot.entity.Bot;
import com.messenger.chat.dto.MessageResponse;
import com.messenger.chat.event.MessageSentEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class BotEventListener {

    private static final Logger log = LoggerFactory.getLogger(BotEventListener.class);

    private final BotRepository botRepository;
    private final BotWebhookDispatcher webhookDispatcher;

    public BotEventListener(BotRepository botRepository, BotWebhookDispatcher webhookDispatcher) {
        this.botRepository = botRepository;
        this.webhookDispatcher = webhookDispatcher;
    }

    @Async
    @EventListener
    public void handleMessageSent(MessageSentEvent event) {
        MessageResponse message = event.getMessage();
        UUID senderId = UUID.fromString(message.senderId());

        for (UUID participantId : event.getParticipantUserIds()) {
            if (participantId.equals(senderId)) continue;

            Bot bot = botRepository.findByUserId(participantId).orElse(null);
            if (bot != null && Boolean.TRUE.equals(bot.getIsActive()) && bot.getWebhookUrl() != null) {
                log.debug("Dispatching webhook to bot {} for message {}", bot.getId(), message.id());
                webhookDispatcher.dispatch(bot, message);
            }
        }
    }
}
