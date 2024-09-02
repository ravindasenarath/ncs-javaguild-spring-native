FROM container-registry.oracle.com/graalvm/jdk:21-ol8
VOLUME /tmp
COPY target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]