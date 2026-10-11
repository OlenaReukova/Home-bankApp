# Home Bank App

## Overview

Home Bank App is a personal banking application built with Spring Boot. It provides user account management, bank
account operations (deposit, withdraw, transfer) and a transaction history, accessible via both a browser-based UI and a
REST API.

The application is intended as a learning and portfolio project demonstrating a layered Spring Boot architecture with
MongoDB persistence, Spring Security, and role-based access control.

---

## Key Features

- User registration and form-based login
- Role-based access control — `USER` and `ADMIN` roles
- Admin account seeded automatically on first startup
- Bank account creation with supported currencies: USD, EUR, GBP
- Deposit, withdrawal and account-to-account transfer operations
- Paginated transaction history per account
- Self-transfer prevention and insufficient-funds validation
- Users can only access their own accounts
- Request correlation via `X-Correlation-ID` header propagation
- Actuator health endpoints (liveness, readiness)
- Browser-based dashboard (Thymeleaf) and a JSON REST API

---

## Tech Stack

| Category         | Technology                                   |
|------------------|----------------------------------------------|
| Language         | Java 25                                      |
| Framework        | Spring Boot 4.1.0                            |
| Persistence      | Spring Data MongoDB                          |
| Security         | Spring Security (form login + HTTP Basic)    |
| Password hashing | BCrypt                                       |
| Frontend         | Thymeleaf + Thymeleaf Spring Security extras | |
| Object mapping   | MapStruct 1.6.3                              |
| Boilerplate      | Lombok                                       |
| Monitoring       | Spring Boot Actuator                         |
| Configuration    | Spring Dotenv (`.env` file support)          |
| Infrastructure   | Docker Compose (MongoDB, RabbitMQ)           |
| Build tool       | Gradle (wrapper included)                    |
| API testing      | Bruno collection                             |

---

## System Requirements

- **Java 25** (JDK)
- **Docker** and **Docker Compose** (to run MongoDB and RabbitMQ)
- A `.env` file in the project root (see below)

---

## Getting Started

### 1. Clone the repository

```bash
git clone <repository-url>
cd Home-BankApp
```

### 2. Configure environment variables

Create a `.env` file in the project root. The application reads it automatically at runtime via Spring Dotenv. The
following variables are required:

```dotenv
# MongoDB connection URI used by the application
MONGODB_LOCAL_URI=mongodb://<username>:<password>@localhost:27017/<database>?authSource=admin

# MongoDB Docker Compose initialisation
MONGO_INITDB_ROOT_USERNAME=<username>
MONGO_INITDB_ROOT_PASSWORD=<password>
MONGO_INITDB_DATABASE=<database>

# RabbitMQ Docker Compose initialisation
RABBITMQ_DEFAULT_USER=<username>
RABBITMQ_DEFAULT_PASS=<password>

# Admin account seeded on first startup
ADMIN_EMAIL=admin@example.com
ADMIN_PASSWORD=<secure-password>
ADMIN_FIRST_NAME=Admin
ADMIN_LAST_NAME=Admin
```

### 3. Start infrastructure

```bash
docker compose up -d
```

This starts MongoDB on port `27017` and RabbitMQ on ports `5672` (AMQP) and `15672` (management UI).

### 4. Build the application

```bash
./gradlew build
```

### 5. Run the application

```bash
./gradlew bootRun
```

Spring Boot Docker Compose integration will detect `compose.yaml` and manage containers automatically during
development. If you have already started them manually in step 3, set `spring.docker.compose.enabled=false` in your
local properties or `.env`.

### 6. Access the application

| Interface       | URL                                   |
|-----------------|---------------------------------------|
| Browser (UI)    | http://localhost:8080/login           |
| Admin dashboard | http://localhost:8080/admin/users     |
| Actuator health | http://localhost:8080/actuator/health |
| RabbitMQ UI     | http://localhost:15672                |

The admin user defined in `.env` is seeded automatically on first startup.

---

## API Endpoints

All REST endpoints return JSON.

### Authentication

The REST API uses HTTP Basic authentication. The browser UI uses form-based login.

### User management — `/bank/**` (ADMIN only)

| Method   | Path               | Description             |
|----------|--------------------|-------------------------|
| `POST`   | `/bank/users`      | Create a user           |
| `GET`    | `/bank/users`      | List all users          |
| `PUT`    | `/bank/users/{id}` | Replace a user          |
| `PATCH`  | `/bank/users/{id}` | Partially update a user |
| `DELETE` | `/bank/users/{id}` | Delete a user           |

### Bank accounts — `/accounts/**` (USER or ADMIN)

| Method | Path                      | Description                                 |
|--------|---------------------------|---------------------------------------------|
| `POST` | `/accounts`               | Create a bank account                       |
| `GET`  | `/accounts/user/{userId}` | List accounts for a user (paginated)        |
| `POST` | `/accounts/{id}/deposit`  | Deposit funds                               |
| `POST` | `/accounts/{id}/withdraw` | Withdraw funds                              |
| `POST` | `/accounts/{id}/transfer` | Transfer funds to another account by number |

### Transactions — `/accounts/**` (USER or ADMIN)

| Method | Path                          | Description                                  |
|--------|-------------------------------|----------------------------------------------|
| `GET`  | `/accounts/{id}/transactions` | List transactions for an account (paginated) |

**Pagination parameters** (all paginated endpoints): `?page=0&size=20&sort=createdAt,desc`

### Actuator

| Method | Path                         | Description       |
|--------|------------------------------|-------------------|
| `GET`  | `/actuator/health`           | Aggregated health |
| `GET`  | `/actuator/health/liveness`  | Liveness probe    |
| `GET`  | `/actuator/health/readiness` | Readiness probe   |

### Request bodies

**Create user / Replace user**

```json
{
  "firstName": "Jane",
  "lastName": "Smith",
  "email": "jane@example.com",
  "phoneNumber": "+44XXXXX"
}
```

**Create account**

```json
{
  "userId": "<user-id>",
  "currency": "GBP",
  "initialBalance": 0
}
```

Supported currencies: `USD`, `EUR`, `GBP`

**Deposit / Withdraw**

```json
{
  "amount": 250.00
}
```

**Transfer**

```json
{
  "toAccountNumber": "ACC-XXXXXXXX",
  "amount": 100.00
}
```

---

## Testing

Run all tests:

```bash
./gradlew test
```

A **Bruno** collection is available in the `bruno/` directory for manual API testing. Open the collection in Bruno and
configure the `local` environment with your `baseUrl`, `userId` and `accountId` variables before use.

---

## Work in Progress

- RabbitMQ Messaging
- Apache Camel and Integration Patterns
- Currency conversion between accounts
- Account statement export
- Frontend improvements and enhanced form validation feedback
