FROM maven as build

COPY . /app


RUN mvn install:install-file -Dfile=app/libs/moise.jar -DgroupId=org.jacamo -DartifactId=moise -Dversion=1.1 -Dpackaging=jar

RUN mvn -f app/pom.xml clean package -DskipTests

FROM openjdk:21-jdk

COPY --from=build /app/target/OMI-0.0.1.jar ./OMI-0.0.1.jar

COPY --from=build /app/src/main/resources/application.properties ./application.properties
COPY --from=build /app/src/main/resources/metadata.ttl ./metadata.ttl
COPY --from=build /app/src/main/resources/org.xml ./org.xml

EXPOSE 5685/udp
EXPOSE 7500/tcp

CMD ["java", "-jar", "OMI-0.0.1.jar", "--spring.config.location=application.properties"]