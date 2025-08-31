FROM openjdk:21-jdk-slim
WORKDIR /app
COPY build/libs/quick-start.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
