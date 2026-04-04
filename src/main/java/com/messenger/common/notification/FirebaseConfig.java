package com.messenger.common.notification;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;

@Configuration
public class FirebaseConfig {

    private static final Logger log = LoggerFactory.getLogger(FirebaseConfig.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${fcm.service-account-json:}")
    private String serviceAccountJson;

    @Value("${fcm.service-account-file:}")
    private String serviceAccountFile;

    @Value("${fcm.project-id:}")
    private String projectId;

    @Value("${fcm.client-email:}")
    private String clientEmail;

    @Value("${fcm.client-id:}")
    private String clientId;

    @Value("${fcm.private-key:}")
    private String privateKey;

    @Value("${fcm.private-key-id:}")
    private String privateKeyId;

    @Bean
    public FirebaseMessaging firebaseMessaging() {
        try {
            String json = buildServiceAccountJson();
            if (json == null) {
                return null;
            }

            GoogleCredentials credentials = GoogleCredentials.fromStream(
                    new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))
            );

            JsonNode root = objectMapper.readTree(json);
            String projId = root.path("project_id").asText();

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(credentials)
                    .setProjectId(projId)
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }

            log.info("Firebase initialized for project: {}", projId);
            return FirebaseMessaging.getInstance();
        } catch (IOException e) {
            log.error("Failed to initialize Firebase: {}", e.getMessage());
            return null;
        }
    }

    private String buildServiceAccountJson() throws IOException {
        if (serviceAccountJson != null && !serviceAccountJson.isBlank()) {
            String trimmed = serviceAccountJson.trim();
            if (trimmed.startsWith("{")) {
                return trimmed;
            }
        }

        if (serviceAccountFile != null && !serviceAccountFile.isBlank()) {
            File file = new File(serviceAccountFile);
            if (file.exists() && file.isFile()) {
                log.info("Loading Firebase credentials from file: {}", serviceAccountFile);
                return Files.readString(file.toPath(), StandardCharsets.UTF_8);
            }
        }

        if (projectId.isBlank() || clientEmail.isBlank() || privateKey.isBlank()
                || clientId.isBlank() || privateKeyId.isBlank()) {
            log.warn("FCM not configured. Use FCM_SERVICE_ACCOUNT_JSON (full JSON) or all FCM_* variables.");
            return null;
        }

        String keyWithNewlines = privateKey.replace("\\n", "\n");
        Map<String, String> creds = Map.of(
                "type", "service_account",
                "project_id", projectId,
                "private_key_id", privateKeyId,
                "private_key", keyWithNewlines,
                "client_email", clientEmail,
                "client_id", clientId,
                "token_uri", "https://oauth2.googleapis.com/token"
        );
        return objectMapper.writeValueAsString(creds);
    }
}
