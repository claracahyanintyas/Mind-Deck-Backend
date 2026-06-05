# STAGE 1: Build the application using JDK 21
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY . .

RUN chmod +x gradlew

RUN ./gradlew bootJar

# STAGE 2: Run the application using JRE 21 (much smaller file size)
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]