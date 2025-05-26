# Elmenus Lite

A lightweight implementation of the Elmenus food ordering concept. This application provides a platform for restaurants to list their menus and for customers to browse and order food.

## Overview

Elmenus Lite is a Spring Boot application that allows:

- Restaurants to register and manage their menus
- Customers to browse restaurants and their menus
- Users to create accounts and place orders
- Address management for delivery locations

## Technologies Used

- **Java 17**
- **Spring Boot 3.4.5**
- **Spring Data JPA** - For database access and ORM
- **PostgreSQL** - As the primary database
- **Hibernate Spatial** - For location-based features
- **Liquibase** - For database migration and version control
- **MapStruct** - For object mapping between DTOs and models
- **Lombok** - For reducing boilerplate code
- **Hibernate Validator** - For input validation
- **Maven** - For dependency management and build

## Project Structure

```
src/main/java/spring/practice/elmenus_lite/
├── apiDto          # API data transfer objects
├── controller      # REST controllers
├── dto             # Internal data transfer objects
├── exception       # Custom exception handlers
├── helperannotations # Custom annotations
├── mapper          # MapStruct mappers
├── model           # Domain entities
├── repository      # JPA repositories
├── service         # Business logic
├── statusCode      # Response status codes
└── util            # Utility classes
```

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- PostgreSQL 12 or higher

### Setup

1. Clone the repository:
```bash
git clone <repository-url>
cd elmenus-lite
```

2. Configure the database connection in `src/main/resources/application.properties` or `application.yml`

3. Build the project:
```bash
./mvnw clean install
```

4. Run the application:
```bash
./mvnw spring-boot:run
```

The application will be available at `http://localhost:8080`

## Features

- User authentication and authorization
- Restaurant and menu management
- Food ordering system
- Cart functionality
- Address management for delivery

## Development

### Running with Docker

The project includes Docker Compose configuration to easily set up the PostgreSQL database.

#### Prerequisites

- Docker and Docker Compose installed on your machine

#### Starting the Database

```bash
# Start the PostgreSQL container
docker-compose up -d postgres

# To check the logs
docker-compose logs -f postgres
```

This will start a PostgreSQL container with the following configuration:
- Database: postgres
- Schema: study
- Username: postgres
- Password: mysecretpassword
- Port: 5432 (mapped to host)

#### Stopping the Database

```bash
docker-compose down
```

To remove the volumes and delete all data:

```bash
docker-compose down -v
```

### Building

```bash
./mvnw clean package
```

### Running Tests

```bash
./mvnw test
```

## License

MIT License

## Contributing

[Add contributing guidelines here]
