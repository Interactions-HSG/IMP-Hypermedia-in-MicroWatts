# Use an official OpenJDK runtime as a parent image
FROM openjdk:21

# Set the working directory in the container
WORKDIR /app

# Copy the .jar file into the container at /app
COPY orgserver.jar orgserver.jar

# Expose the port your application will run on (change if needed)
EXPOSE 5683/udp

# Run the .jar file
ENTRYPOINT ["java", "-jar", "orgserver.jar"]
