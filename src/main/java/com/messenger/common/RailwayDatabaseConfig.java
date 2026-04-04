package com.messenger.common;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Конвертирует Railway DATABASE_URL (postgresql://...) в JDBC формат для Spring Boot.
 * Railway даёт postgresql://user:pass@host:port/db, Spring Boot требует jdbc:postgresql://host:port/db
 */
public class RailwayDatabaseConfig implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        // Railway: публичный URL для загруженных файлов (голосовые, фото и т.д.)
        String publicDomain = environment.getProperty("RAILWAY_PUBLIC_DOMAIN");
        if (publicDomain != null && !publicDomain.isBlank()) {
            Map<String, Object> fileProps = new HashMap<>();
            fileProps.put("file.public-base-url", "https://" + publicDomain + "/uploads");
            // Railway: без Volume файловая система read-only. Нужен Volume с mount path /app/uploads
            String uploadDir = environment.getProperty("FILE_UPLOAD_DIR");
            if (uploadDir == null || uploadDir.isBlank()) {
                fileProps.put("file.upload-dir", "/app/uploads");
            }
            environment.getPropertySources().addFirst(
                    new MapPropertySource("railwayFileUrl", fileProps)
            );
        }

        String databaseUrl = environment.getProperty("DATABASE_URL");
        if (databaseUrl == null || databaseUrl.isBlank()) {
            return;
        }
        if (!databaseUrl.startsWith("postgresql://") && !databaseUrl.startsWith("postgres://")) {
            return;
        }
        try {
            URI uri = URI.create(databaseUrl.replace("postgres://", "postgresql://"));
            String userInfo = uri.getUserInfo();
            String host = uri.getHost();
            int port = uri.getPort() > 0 ? uri.getPort() : 5432;
            String path = uri.getPath();
            String database = path != null && path.length() > 1 ? path.substring(1) : "railway";

            String user = "";
            String password = "";
            if (userInfo != null && !userInfo.isEmpty()) {
                try {
                    userInfo = java.net.URLDecoder.decode(userInfo, java.nio.charset.StandardCharsets.UTF_8);
                } catch (Exception ignored) {}
                int colon = userInfo.indexOf(':');
                if (colon > 0) {
                    user = userInfo.substring(0, colon);
                    password = userInfo.substring(colon + 1);
                } else {
                    user = userInfo;
                }
            }

            String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s", host, port, database);

            Map<String, Object> props = new HashMap<>();
            props.put("spring.datasource.url", jdbcUrl);
            props.put("spring.datasource.username", user);
            props.put("spring.datasource.password", password);

            environment.getPropertySources().addFirst(
                    new MapPropertySource("railwayDb", props)
            );
        } catch (Exception e) {
            System.err.println("[RailwayDatabaseConfig] Failed to parse DATABASE_URL: " + e.getMessage());
        }
    }
}
