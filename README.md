# TVMaze Middleware API

REST API developed with Java and Spring Boot that works as a middleware for the TVMaze API.

The application provides show search and detail endpoints, MongoDB caching, and support for comments and ratings associated with shows.

## Technologies

- Java 17
- Spring Boot
- Spring Web
- Spring Data MongoDB
- MongoDB Atlas
- Maven
- JUnit 5
- Mockito

## Requirements

- Java 17+
- Maven
- MongoDB Atlas database

## Configuration

The application requires the following environment variable:

```bash
MONGODB_URI=<your-mongodb-connection-string>
```

TVMaze base URL is configured in `application.properties`:

```properties
spring.application.name=${APP_NAME:tvmaze-middleware-api}
server.port=${SERVER_PORT:8080}
tvmaze.base-url=${TVMAZE_BASE_URL:https://api.tvmaze.com}
spring.data.mongodb.uri=${MONGODB_URI}
```

## Run

```bash
mvn spring-boot:run
```

The application runs by default on:

```text
http://localhost:8080
```

## Endpoints

### Search shows

```http
GET /api/v1/shows/search?search_query=batman
```

Returns shows matching the search criteria, including their stored comments.

### Get show

```http
GET /api/v1/shows/{showId}
```

Returns the complete show information and its comments. Show information is retrieved from MongoDB cache when available.

### Add comment

```http
POST /api/v1/comments
Content-Type: application/json

{
  "show_id": 1,
  "comment": "Great show",
  "rating": 5
}
```

`rating` accepts values from `0` to `5`.

## Tests

```bash
mvn clean test
```