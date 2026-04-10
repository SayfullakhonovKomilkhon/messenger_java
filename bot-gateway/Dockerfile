# Build only bot-gateway JAR (needs full Gradle tree: settings.gradle includes bot-gateway)
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

RUN chmod +x gradlew && ./gradlew dependencies --no-daemon

COPY src src
COPY bot-gateway/build.gradle bot-gateway/
COPY bot-gateway/src bot-gateway/src

RUN ./gradlew :bot-gateway:bootJar --no-daemon -x test

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup && \
    apk add --no-cache su-exec

COPY --from=build /app/bot-gateway/build/libs/bot-gateway-0.0.1-SNAPSHOT.jar app.jar

ENV JAVA_OPTS="-Xmx256m -Xms128m"

EXPOSE 3001

ENTRYPOINT ["sh", "-c", "exec su-exec appuser java $JAVA_OPTS -jar -Dserver.port=${PORT:-3001} app.jar"]
