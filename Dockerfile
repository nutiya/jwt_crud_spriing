FROM openjdk:22-jdk
WORKDIR /app
COPY target/student_management.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]