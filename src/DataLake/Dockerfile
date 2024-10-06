FROM maven as build

COPY . /app

RUN mvn -f app/pom.xml clean package -DskipTests

FROM openjdk:21-jdk

COPY --from=build /app/target/DataLake-0.0.1.jar ./DataLake-0.0.1.jar

COPY --from=build /app/src/main/resources/application.properties ./application.properties
COPY --from=build /app/src/main/resources/metadata.ttl ./metadata.ttl

CMD ["java", "-jar", "DataLake-0.0.1.jar", "--spring.config.location=application.properties"]
