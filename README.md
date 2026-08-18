# Order & Inventory Service

A Spring Boot backend that manages product inventory and order placement, built to solve a real concurrency problem: **preventing overselling when multiple customers try to buy the same limited-stock item at once.**

## The Problem

In any e-commerce system, if two customers try to buy the last unit of a product at the exact same moment, a naive implementation can let both orders succeed — resulting in negative stock and an oversold item. This project solves that using **optimistic locking**, and proves it works with a real concurrency test.

## Key Features

- **Product & Order management** — REST APIs to create and list products, and place orders
- **Optimistic locking** — every product has a `@Version` field. When two requests try to update the same product simultaneously, only one succeeds; the other is safely rejected instead of corrupting the stock count
- **Verified under real concurrency** — includes a JUnit test that fires two threads at the same product with stock = 1, and asserts exactly one order succeeds and one fails

## Tech Stack

- Java 21, Spring Boot 4
- Spring Data JPA + PostgreSQL
- Docker (for running Postgres locally)
- JUnit 5 for concurrency testing

## Architecture

Controller → Service → Repository → PostgreSQL

- `ProductController` / `OrderController` — expose REST endpoints
- `OrderService` — contains the core business logic: stock check, stock deduction, optimistic-lock-protected save
- `ProductRepository` / `OrderRepository` — Spring Data JPA repositories, no manual SQL

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/products` | Create a new product |
| GET | `/products` | List all products |
| POST | `/orders` | Place an order (deducts stock, protected by optimistic locking) |

### Example: Create a product

curl -X POST http://localhost:8080/products \
  -H "Content-Type: application/json" \
  -d '{"name": "Wireless Mouse", "price": 799.0, "stockQuantity": 50}'

### Example: Place an order

curl -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -d '{"productId": 1, "quantity": 5}'

## Running Locally

1. Start Postgres in Docker:

docker run --name order-db -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=orderdb -p 5432:5432 -d postgres:16

2. Run the app:

./mvnw spring-boot:run

3. Run the concurrency test:

./mvnw test -Dtest=ConcurrencyTest

## What This Demonstrates

This project was built to go beyond a typical CRUD demo — it targets a genuine distributed-systems problem (race conditions on shared state) and backs the solution with an automated test proving it works, rather than just claiming it does.
