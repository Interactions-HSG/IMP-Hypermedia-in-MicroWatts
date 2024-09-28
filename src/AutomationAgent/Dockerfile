FROM eclipse-temurin:21

# AutomationAgent build configuration
ENV AUTOMATION_AGENT_VERSION 0.0.0

# Build environment preparation
ENV LANG C.UTF-8

# Copy the src directory
RUN mkdir /opt/src/
COPY ./src/ /opt/src/

# Make gradlew executable
RUN chmod +x /opt/src/automationagent/gradlew

# Set the working directory
WORKDIR /opt/src/automationagent
# The default http port
EXPOSE 8081

# Set the entrypoint to run gradlew
# ENTRYPOINT ["./gradlew"]
# CMD ["-q", "--console=plain"]
# We can also just start it from inside the container