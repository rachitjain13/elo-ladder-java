# Stage 1: Build JAR using Maven with Java 17
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder

WORKDIR /build

# Cache dependencies layer
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Copy source code and build package without tests in builder
COPY src ./src
RUN mvn -B clean package -DskipTests

# Stage 2: Minimal runtime image
FROM eclipse-temurin:17-jre-alpine AS runner

WORKDIR /app

# Run as non-root user (security requirement)
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy built jar from builder stage
COPY --from=builder /build/target/ladder-1.0.0.jar app.jar

# Set correct ownership
RUN chown -R appuser:appgroup /app

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
