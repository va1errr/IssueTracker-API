# Issue Tracker API

A RESTful Issue Tracker backend built with **Spring Boot** and **PostgreSQL**, designed with clean architecture and production-ready practices.

## Features
- User, Project, and Issue domain models
- Enum-based domain constraints (status, priority, roles)
- Clean architecture with DTO layer
- Unified API response structure
- PostgreSQL database (Dockerized)
- Flyway database migrations
- JPA entity relationships (One-to-Many / Many-to-One)

## Architecture

1. **Client** → Makes requests
2. **Controller** → Handles HTTP requests/responses
3. **DTO** → Data Transfer Object
4. **Service** → Business logic layer
5. **Entity** → Domain model
6. **Repository** → Data access layer
7. **Database** → Persistence storage

## API design

### Success response

```json
{
  "status": 200,
  "success": true,
  "message": "<Relevant Message>",
  "data": ["<Relevant data>"],
  "timestamp": "<Timestamp>"
}
```

## Database Initialization

This application uses PostgreSQL with Flyway-managed migrations.

On startup:
- Flyway applies SQL migrations from `src/main/resources/db/migration`
- Hibernate validates entity mappings against the migrated schema (`ddl-auto=validate`)

## Tech Stack
- **Java 25**
- **Spring Boot 4.0.5**
- **Spring Web**
- **Spring Data JPA**
- **PostgreSQL**
- **Flyway**
- **Lombok**
- **Maven**
- **Jakarta Validation**
- **Docker**

## Getting Started

### Prerequisites
- Java 25 or higher
- Docker

### Setting and configuring the environment variables
Copy the `.env.example` to `.env` and edit.
```.properties
POSTGRES_PORT=5432      #Set free port for listening
POSTGRES_DB=issue_tracker      #Set data base name
POSTGRES_USER=postgres      #Set postgres username
POSTGRES_PASSWORD=postgres      #Set postgres username password
```

Export environment variables to Spring Application.

### Running the Application

```bash
# 1) Run the postgres docker container
docker compose up -d

# 2) Verify it's running
docker ps

# 3) Build and run the application
./mvnw clean install
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`.

Database connection is configured in `src/main/resources/application.properties`:

If Flyway reports `Found non-empty schema(s) "public" but no schema history table`, use a clean schema in dev:

```sql
DROP SCHEMA public CASCADE;
CREATE SCHEMA public;
GRANT ALL ON SCHEMA public TO va1err;
GRANT ALL ON SCHEMA public TO public;
```

## Project Structure

```
src/main/java/com/va1err/IssueTracker/
├── controllers/     # REST controllers
├── services/        # Business logic
├── repositories/    # Data access layer
├── models/          # JPA entities
├── dto/             # Data transfer objects
└── enums/           # Enumerations
```