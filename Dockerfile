FROM openjdk:17
ADD target/testapp-0.0.1-SNAPSHOT.jar testapp-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "testapp-0.0.1-SNAPSHOT.jar"]