FROM gradle:8.2.1-jdk17 AS builder

WORKDIR /app

COPY build.gradle settings.gradle ./
COPY gradle ./gradle
COPY src/ ./src
COPY api/ ./api

RUN gradle build --stacktrace --no-daemon -i

RUN rm /app/build/libs/*-plain.jar

FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]