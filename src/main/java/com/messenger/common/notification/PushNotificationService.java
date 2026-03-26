package com.messenger.common.notification;

import com.google.firebase.messaging.*;
import com.messenger.user.UserRepository;
import com.messenger.user.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class PushNotificationService {

    private static final Logger log = LoggerFactory.getLogger(PushNotificationService.class);

    private final FirebaseMessaging firebaseMessaging;
    private final UserRepository userRepository;

    public PushNotificationService(@Nullable FirebaseMessaging firebaseMessaging,
                                    UserRepository userRepository) {
        this.firebaseMessaging = firebaseMessaging;
        this.userRepository = userRepository;
    }

    @Async
    public void sendPush(UUID userId, String title, String body, Map<String, String> data) {
        if (firebaseMessaging == null) {
            log.info("[FCM] Push skipped: Firebase not configured");
            return;
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null || user.getFcmToken() == null || user.getFcmToken().isBlank()) {
            return;
        }

        String pushType = data.getOrDefault("type", "");
        boolean isCall = "INCOMING_CALL".equals(pushType);

        Message message = Message.builder()
                .setToken(user.getFcmToken())
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .setAndroidConfig(buildAndroidConfig(isCall))
                .setApnsConfig(buildApnsConfig(title, body, isCall, data))
                .putAllData(data != null ? data : Map.of())
                .build();

        try {
            String messageId = firebaseMessaging.send(message);
            log.info("[FCM] Push sent to user {}: {}", userId, messageId);
        } catch (FirebaseMessagingException e) {
            String errorCode = e.getMessagingErrorCode() != null ? e.getMessagingErrorCode().name() : "UNKNOWN";
            if ("UNREGISTERED".equals(errorCode) || "INVALID_ARGUMENT".equals(errorCode)) {
                log.info("[FCM] Token invalid for user {}, clearing", userId);
                user.setFcmToken(null);
                userRepository.save(user);
            } else {
                log.warn("[FCM] Failed to send push to user {}: {} ({})", userId, e.getMessage(), errorCode);
            }
        }
    }

    private AndroidConfig buildAndroidConfig(boolean isCall) {
        String channelId = isCall ? "calls" : "messages";
        AndroidConfig.Builder builder = AndroidConfig.builder()
                .setPriority(AndroidConfig.Priority.HIGH)
                .setNotification(AndroidNotification.builder()
                        .setChannelId(channelId)
                        .setSound("default")
                        .setDefaultVibrateTimings(true)
                        .setPriority(isCall
                                ? AndroidNotification.Priority.MAX
                                : AndroidNotification.Priority.HIGH)
                        .build());

        if (isCall) {
            builder.setTtl(30 * 1000);
        }

        return builder.build();
    }

    private ApnsConfig buildApnsConfig(String title, String body, boolean isCall, Map<String, String> data) {
        Aps.Builder apsBuilder = Aps.builder()
                .setSound(isCall ? "ringtone.caf" : "default")
                .setMutableContent(true)
                .setContentAvailable(true);

        if (isCall) {
            apsBuilder.setCategory("INCOMING_CALL");
        } else {
            apsBuilder.setCategory("NEW_MESSAGE");
            apsBuilder.setBadge(1);
        }

        ApsAlert alert = ApsAlert.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        apsBuilder.setAlert(alert);

        ApnsConfig.Builder apnsBuilder = ApnsConfig.builder()
                .setAps(apsBuilder.build())
                .putHeader("apns-priority", isCall ? "10" : "10")
                .putHeader("apns-push-type", "alert");

        if (data != null) {
            data.forEach(apnsBuilder::putCustomData);
        }

        return apnsBuilder.build();
    }
}
