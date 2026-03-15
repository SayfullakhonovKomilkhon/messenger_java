# Stage 1: Build
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Copy Gradle files first for better layer caching
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

# Download dependencies (cached if build files don't change)
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon

# Copy source and build
COPY src src
RUN ./gradlew bootJar --no-daemon -x test

# Stage 2: Run
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Create non-root user + su-exec for privilege drop
RUN addgroup -S appgroup && adduser -S appuser -G appgroup && \
    apk add --no-cache su-exec

# Copy built JAR from build stage
COPY --from=build /app/build/libs/messenger-backend-0.0.1-SNAPSHOT.jar app.jar

# Create uploads dir (Volume на Railway примонтирует сюда)
RUN mkdir -p /app/uploads && chown -R appuser:appgroup /app

# Railway sets PORT automatically
ENV JAVA_OPTS="-Xmx512m -Xms256m"

EXPOSE 3000
# chown для Volume (монтируется как root); su-exec переключает на appuser
ENTRYPOINT ["sh", "-c", "chown -R appuser:appgroup /app/uploads 2>/dev/null || true; exec su-exec appuser java $JAVA_OPTS -jar -Dserver.port=${PORT:-3000} app.jar"]
