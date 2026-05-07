FROM openjdk:21-ea-1-jdk-slim
COPY target/seenit-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]