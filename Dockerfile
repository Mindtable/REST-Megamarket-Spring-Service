FROM openjdk:17

COPY build/libs/REST-Megamarket-Spring-Service-1.0.0-RELEASE-boot.jar application.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "application.jar"]

HEALTHCHECK --interval=1s CMD curl -X GET  http://localhost:8080/init || exit 1