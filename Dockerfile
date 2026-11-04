FROM amazoncorretto:21
VOLUME /tmp
COPY target/set08103-team7-pipeline-*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
