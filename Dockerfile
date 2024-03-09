# Stage 1: Build the application
FROM maven:3.8.1-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -f pom.xml clean install package

# Stage 2: Run the application
FROM alpine:latest
LABEL maintainer="burakugar77@gmail.com"
RUN apk --no-cache add openjdk17
VOLUME /tmp
EXPOSE 8080
COPY --from=build /app/target/*.jar house-hunter-be.jar
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/house-hunter-be.jar"]
