FROM eclipse-temurin:25-jdk AS build
WORKDIR /app
COPY . .

# FIX: Force executable permissions on the Gradle wrapper script
RUN chmod +x gradlew

RUN ./gradlew bootJar