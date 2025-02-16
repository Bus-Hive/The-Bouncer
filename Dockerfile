FROM ubuntu:latest
LABEL authors="jakubolszewski"

ENTRYPOINT ["top", "-b"]

# Use a JDK base image for building and running the application
FROM gradle:8.4-jdk17 AS builder

# Set the working directory inside the container
WORKDIR /app

# Copy project files
COPY . /app

RUN chmod +x ./gradlew

# Build the application using Gradle
RUN gradle build -x test

# Use a lightweight JDK image for running the application
FROM openjdk:17-jdk-slim

# Set the working directory for the runtime container
WORKDIR /app

# Copy the built JAR file from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose the application port
EXPOSE 9000

# Set environment variables for the application (optional)
ENV PORT=9000

# Define the entrypoint to run the Ktor server
CMD ["java", "-jar", "/app/app.jar"]
