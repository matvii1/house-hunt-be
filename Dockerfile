#
# Build stage
#
FROM maven:3.8.1-openjdk-17-slim AS build
COPY src /home/app/src
COPY .env /home/app/
ARG JWT_SECRET_KEY=${JWT_SECRET_KEY}
WORKDIR /home/app
COPY pom.xml /home/app
COPY src/main/resources/db/scripts/InitialiseDbData.sql /docker-entrypoint-initdb.d/
RUN mvn -f /home/app/pom.xml clean package
# Set environment variables from .env

FROM openjdk:17.0.1-jdk-slim
COPY --from=build /home/app/target/house-hunter-be-0.0.1-SNAPSHOT.jar  /usr/local/lib/house-hunter-be.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/local/lib/house-hunter-be.jar"]