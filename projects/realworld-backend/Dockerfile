# syntax=docker/dockerfile:1
FROM java:latest

WORKDIR /app

COPY target/realworld-backend.jar /app/realworld-backend.jar

EXPOSE 6005

CMD ["java", "-server", "-jar", "/app/realworld-backend.jar"]