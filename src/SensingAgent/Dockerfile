FROM gradle:8.10.2-jdk21 AS build

# Set the working directory
WORKDIR /app

# Copy the src directory
COPY ./simulated .

# Build the project
RUN gradle shadowJar --no-daemon

# Set the base image
FROM eclipse-temurin:21

# Set the working directory
WORKDIR /app/

# Copy the build artifacts
COPY --from=build /app .

# Expose the port
EXPOSE 8080

WORKDIR /app/app/build/libs
# Set the default command
CMD ["java", "-jar", "app-all.jar"]