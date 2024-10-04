FROM eclipse-temurin:21-jdk

WORKDIR /app/

COPY /src/automationagent .

RUN ./gradlew clean build --no-daemon

EXPOSE 8081

WORKDIR /app

CMD ["./gradlew"]