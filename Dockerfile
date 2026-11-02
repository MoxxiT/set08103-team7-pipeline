FROM amazoncorretto:21
COPY target/set08103-team7-pipeline-*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]