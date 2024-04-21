During the dploymen, hardcoded env variables should be replaced by
either kubernetes secrets or docker swarm secrets.


docker-compose down -v
docker-compose up --build
docker exec -it db psql -U postgres

# House Hunter Backend

This is the backend service for the House Hunter application. It's a Spring Boot application that uses PostgreSQL as its database.


## Prerequisites

Before you begin, ensure you have met the following requirements:

- You have installed the latest version of Java, Maven, Docker, and Docker Compose.
- You have a Mac machine. If you have a Windows machine, please use appropriate commands or tools.

## Building the Project

To build the project, run the following command in your terminal:

```sh
./mvnw clean package
```

This will create a JAR file in the target directory.

Running the Project
To run the project, you can use Docker Compose:
```
docker-compose up --build
```
This will start the application and the PostgreSQL database.

Environment Variables
The application uses the following environment variables:
```
SPRING_DATASOURCE_URL: The JDBC URL for the PostgreSQL database.
SPRING_DATASOURCE_USERNAME: The username for the PostgreSQL database.
SPRING_DATASOURCE_PASSWORD: The password for the PostgreSQL database.
```
These variables are set in the docker-compose.yml file.

API Endpoints
The application exposes the following REST API endpoints:

/houses: GET, POST, PUT, DELETE operations for houses.
/users: GET, POST, PUT, DELETE operations for users.


API Documentation
The API documentation is available at Swagger UI when the application is running.
Hit the following url:
```
http://localhost:8080/swagger-ui/index.html
```
