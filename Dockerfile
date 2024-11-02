FROM openjdk:21-slim
WORKDIR /app
COPY build/libs/testProjectBack-0.0.1-SNAPSHOT.jar /app/ProjectBack.jar