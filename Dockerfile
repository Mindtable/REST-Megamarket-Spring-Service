FROM openjdk:17

COPY build/libs/YandexBackendPart2Spring-0.0.1-SNAPSHOT-boot.jar application.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "application.jar"]