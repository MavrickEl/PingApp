FROM openjdk:21

WORKDIR /app

COPY target/PingApp-0.0.1.jar /app/app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]