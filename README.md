# HotelBookingApp

A Spring Boot hotel booking application backed by PostgreSQL.

## Tech stack

- Java 21
- Spring Boot 3.5.x
- Spring Web
- Spring Data JPA
- Validation
- PostgreSQL
- Maven
- Lombok

## Repository goals

This repository is set up for incremental development and frequent GitHub pushes:

- keep source code and config under version control
- keep local secrets and machine-specific files out of Git
- make it easy to run the app against a local PostgreSQL instance

## Project structure

- `src/main/java` — application source code
- `src/main/resources` — application configuration
- `src/test/java` — tests

## Prerequisites

- Java 21
- Maven 3.9+ or the included Maven wrapper
- PostgreSQL 15+ or a compatible Docker container

## PostgreSQL setup

If you want to use Docker, start a local PostgreSQL container like this:

```bash
docker run -d \
  --name hotel_booking_postgres \
  -e POSTGRES_DB=hotel_booking_db \
  -e POSTGRES_USER=root \
  -e POSTGRES_PASSWORD=root \
  -p 5432:5432 \
  postgres:latest
```

If the container already exists, restart it instead:

```bash
docker start hotel_booking_postgres
```

## Local configuration

The default `src/main/resources/application.yaml` reads database settings from environment variables:

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`

A sample file is provided in `.env.example`. Copy it to `.env` if you want to manage local values in one place:

```bash
cp .env.example .env
```

`.env` is ignored by Git, so your local settings stay private.

## Running the app

Using the Maven wrapper:

```bash
./mvnw spring-boot:run
```

## Running tests

```bash
./mvnw test
```