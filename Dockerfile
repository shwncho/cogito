FROM openjdk:11

WORKDIR /cogito

COPY cogito-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]