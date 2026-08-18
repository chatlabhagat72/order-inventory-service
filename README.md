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