# Stage 1: Build the jar inside Docker
FROM maven:3.9.2-eclipse-temurin-17 AS build
WORKDIR /app

# Copy Maven config and source code
COPY pom.xml .
COPY src ./src

# Build the jar (skip tests for faster build)
RUN mvn clean package -DskipTests

# Stage 2: Run the jar using OpenJDK
FROM openjdk:22-jdk
WORKDIR /app

# COPY jar from the build stage, not local target folder
COPY --from=build /app/target/student_management.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]