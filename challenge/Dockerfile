FROM openjdk:21-jdk-slim
ARG JAR_FILE=target/challenge-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} challenge.jar
EXPOSE 8080
ENTRYPOINT [ "java", "-jar", "/challenge.jar" ]
