# Hotel Booking App

A production-style REST API for hotel booking built with Spring Boot. Supports hotel management, room inventory, dynamic pricing, user authentication, and Stripe payment integration.

## Tech Stack

- **Java 21** / **Spring Boot 3.5**
- **Spring Security** — JWT-based authentication & role-based authorization
- **Spring Data JPA** — PostgreSQL
- **Stripe** — Payment processing via Checkout Sessions + Webhooks
- **SpringDoc OpenAPI** — Swagger UI for API documentation
- **Lombok** / **ModelMapper**

## Features

- **Auth** — Signup, login (JWT access + refresh token via HTTP-only cookie)
- **Hotel Management** — CRUD for hotels and rooms (admin / `HOTEL_MANAGER` role)
- **Room Inventory** — Daily inventory tracking with surge pricing and availability control
- **Dynamic Pricing** — Pluggable pricing strategies (occupancy, surge, holiday, urgency)
- **Hotel Search** — Public search by city, dates, and room count with average pricing
- **Bookings** — Initialize → add guests → pay → confirm flow
- **Payments** — Stripe Checkout Sessions with webhook-based confirmation
- **Reports** — Revenue and booking reports per hotel with date filtering

## API Documentation

Once the app is running, Swagger UI is available at:

```
http://localhost:8080/api/v1/swagger-ui.html
```

## Getting Started

### Prerequisites

- Java 21
- Maven 3.9+ (or use the included `mvnw` wrapper)
- PostgreSQL 15+
- Stripe account (for payment features)

### 1. Database Setup

Start a PostgreSQL instance. With Docker:

```bash
docker run -d \
  --name hotel_booking_postgres \
  -e POSTGRES_DB=hotel_booking_db \
  -e POSTGRES_USER=root \
  -e POSTGRES_PASSWORD=root \
  -p 5432:5432 \
  postgres:latest
```

### 2. Environment Variables

Copy the example env file and fill in your values:

```bash
cp .env.example .env
```

| Variable | Description |
|---|---|
| `DB_URL` | JDBC connection string |
| `DB_USERNAME` | Database username |
| `DB_PASSWORD` | Database password |
| `JWT_SECRET_KEY` | Secret key for signing JWTs |
| `STRIPE_SECRET_KEY` | Stripe API secret key |
| `STRIPE_WEBHOOK_SECRET` | Stripe webhook signing secret |

### 3. Run

```bash
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080/api/v1`.

## Project Structure

```
src/main/java/com/nr3101/hotelbookingapp/
├── advice/          # Global exception handling & response wrapping
├── config/          # App configuration (ModelMapper, OpenAPI, Stripe)
├── controller/      # REST controllers
├── dto/             # Request & response DTOs
├── entity/          # JPA entities & enums
├── repository/      # Spring Data JPA repositories
├── security/        # JWT auth filter, service & security config
├── service/         # Business logic (interfaces + implementations)
├── strategy/        # Dynamic pricing strategies
└── util/            # Utility classes
```

## License

This project is for learning and portfolio purposes.