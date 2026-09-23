# ---------- Stage 1: build the jar ----------
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

# copy Gradle files first so dependency downloads are cached between builds
COPY gradlew settings.gradle.kts build.gradle.kts gradle.properties ./
COPY gradle ./gradle
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew && ./gradlew dependencies --no-daemon -q > /dev/null

COPY src ./src
RUN ./gradlew shadowJar --no-daemon -x test

# ---------- Stage 2: small image that only runs the jar ----------
FROM eclipse-temurin:25-jre
WORKDIR /app

# don't run as root inside the container
RUN useradd --system --uid 1001 appuser
USER appuser

COPY --from=build /app/build/libs/*-all.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]
