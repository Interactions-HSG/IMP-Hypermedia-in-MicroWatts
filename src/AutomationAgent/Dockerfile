# Official Gradle image
FROM gradle AS builder

# Set the working directory inside the container
WORKDIR /app

# Copy application source code
COPY src/automationagent /app

# Download dependencies
RUN ./gradlew clean build --no-daemon

# Expose the application's port
EXPOSE 8081

# Run the application
ENTRYPOINT ["./gradlew", "run"]

