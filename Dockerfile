# Multi-stage build for Huari Service (Java 25 / Spring Boot 4.x)

# ---------------------------------------------------------
# Stage 1: Build stage
# ---------------------------------------------------------
FROM eclipse-temurin:25-jdk AS builder
WORKDIR /build

# Copy Maven wrapper configuration and dependencies definition
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Normalize line endings in case checkout was performed on Windows
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw

# Download dependencies in an isolated layer for build caching
RUN ./mvnw dependency:go-offline -B

# Copy application source code
COPY src ./src

# Compile and package application (skipping tests during image assembly)
RUN ./mvnw clean package -DskipTests -B

# ---------------------------------------------------------
# Stage 2: Minimal Runtime stage
# ---------------------------------------------------------
FROM eclipse-temurin:25-jre AS runtime
WORKDIR /app

# Create dedicated non-root system group and user
RUN groupadd -r huari && useradd -r -g huari -d /app huari

# Copy built artifact from builder stage with correct ownership
COPY --from=builder --chown=huari:huari /build/target/*.jar /app/app.jar

# Run as non-privileged user for enhanced container security
USER huari

# Default application port
EXPOSE 8080

# Configure JVM flags optimized for container environments
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Launch application
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
