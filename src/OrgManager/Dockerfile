FROM gradle:8.10.2-jdk21 AS build

WORKDIR /app

COPY . .

WORKDIR /app/OrgManagerGradle

RUN gradle shadowJar --no-daemon

# Set the base image
FROM eclipse-temurin:21

# Set the working directory
WORKDIR /app

# Copy the build artifacts
COPY --from=build /app/OrgManagerGradle/app/build/libs .
COPY --from=build /app/org.xml .
COPY --from=build /app/OrgManagerGradle/app/src/main/resources .

WORKDIR /app
# Set the default command
CMD ["java", "-jar", "app-all.jar"]