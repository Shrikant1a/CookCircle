# ==============================================================================
# CookCircle Backend - Spring Boot 3.3.0 Multi-Stage Dockerfile
# ==============================================================================

# Stage 1: Build the runnable JAR
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

# Cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B || true

# Copy source code and package
COPY src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Lightweight Alpine JRE Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Create non-root system user for security
RUN addgroup -S cookgroup && adduser -S cookuser -G cookgroup
USER cookuser:cookgroup

# Copy compiled JAR from builder stage
COPY --from=builder --chown=cookuser:cookgroup /app/target/cookcircle-backend-1.0.0.jar app.jar

# Configuration environment defaults
ENV SPRING_PROFILES_ACTIVE=prod \
    SERVER_PORT=8080 \
    JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8080/api/v1/societies || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

