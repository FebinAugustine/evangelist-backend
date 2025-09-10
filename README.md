# Kotlin Spring Boot Authentication Service

A modern, secure, and scalable authentication and authorization microservice built with Kotlin, Spring Boot, and PostgreSQL. This project is designed following Clean Architecture and SOLID principles to be a robust foundation for any application ecosystem.

---

## About The Project

This service provides a centralized and decoupled identity provider that simplifies user management for web, mobile, and desktop applications. It offers a complete solution for user authentication, including local (email/password) registration, social login (Google), and comprehensive password management.

### Key Features

*   **Local & Social Authentication:** Standard email/password registration and login, plus OAuth2 integration with Google.
*   **Stateless API:** Uses JSON Web Tokens (JWTs) stored in secure, HTTP-only cookies for secure and scalable communication.
*   **Clean Architecture:** A strict separation of concerns between the Domain, Application, Infrastructure, and Presentation layers for high maintainability and testability.
*   **Complete User Lifecycle Management:** Includes account verification, password reset, and user profile management.
*   **Role-Based Access Control (RBAC):** Secure admin-only endpoints for user management.
*   **Production-Ready Practices:** Includes database migrations with Liquibase, rate limiting, and comprehensive exception handling.

### Core Technologies

*   **Language:** Kotlin
*   **Framework:** Spring Boot 3 & Spring Security 6
*   **Database:** PostgreSQL
*   **Build Tool:** Gradle (with Kotlin DSL)
*   **Data Access:** Spring Data JPA & Hibernate
*   **Migrations:** Liquibase
*   **API:** RESTful with JWT

---

## Project Documentation

This project is extensively documented to provide a clear understanding of its architecture, implementation, and functionality. 

*   **[Product Requirements Document (Kotlin-SB-Grad.md)](Kotlin-SB-Grad.md):** The core architectural blueprint and functional specification for the project.

*   **[Step-by-Step Implementation Guide (implementation_steps.md)](implementation_steps.md):** A detailed guide that walks through the entire process of building this service from the ground up.

*   **[Key Architectural Flows (ArchitecturalFlows.md)](ArchitecturalFlows.md):** A deep dive into how the different layers of the application collaborate to fulfill user requests, from registration to token refresh.

---

## Getting Started

To get a local copy up and running, follow these simple steps.

### Prerequisites

*   JDK 21 or higher
*   A running PostgreSQL instance

### Installation & Setup

1.  **Clone the repository:**
    ```sh
    git clone <your-repo-url>
    ```
2.  **Create the database:**
    Connect to your PostgreSQL instance and run:
    ```sql
    CREATE DATABASE authdb;
    ```
3.  **Configure the application:**
    Open `src/main/resources/application.properties` and update the `spring.datasource.*` properties with your PostgreSQL connection details. You will also need to configure your SMTP server details for email sending.

4.  **Run the application:**
    Use the Gradle wrapper to start the service:
    ```sh
    ./gradlew bootRun
    ```

The service will be available at `http://localhost:8080`.
