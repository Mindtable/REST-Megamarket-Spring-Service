FROM openjdk:17

EXPOSE 8080

ENTRYPOINT ["java", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-Djava.security.egd=file:/dev/./urandom","-jar","build/libs/YandexBackendPart2Spring-0.0.1-SNAPSHOT-boot.jar"]
