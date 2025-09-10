# PRD: Kotlin Authentication Service

**Author:** Febin Augustine  
**Version:** 1.1 (Live)  
**Status:** Implemented

---

## 1. Introduction

### 1.1. Purpose

This document outlines the requirements for a modern, secure, and scalable Authentication and Authorization (Auth) Service. It is designed as a foundational microservice for a broad ecosystem of applications, including web, mobile, and desktop clients. This PRD serves as the architectural blueprint and functional specification for the project, emphasizing clean architecture, SOLID principles, and production-ready practices.

### 1.2. Vision

To create a centralized, decoupled, and highly secure identity provider that simplifies user management for all current and future applications. The service will offer a seamless user experience through a variety of authentication methods while providing administrators with robust tools for user management.

---

## 2. Architectural Vision & Principles

This project will be built upon a foundation of modern software engineering principles to ensure it is maintainable, testable, and scalable.

### 2.1. Clean Architecture

The service will follow the principles of **Clean Architecture**. This means the codebase will be separated into distinct layers, with a strict dependency rule: dependencies can only point inwards. This decouples the core business logic from external concerns like the database, framework, and UI.

*   **Domain Layer:** Contains the core business entities and logic (e.g., the `User` class, business rules). It has zero dependencies on any other layer.
*   **Application Layer:** Contains the application-specific business logic (use cases). It orchestrates the flow of data between the domain and infrastructure layers.
*   **Infrastructure Layer:** Contains the implementations of external concerns, such as database repositories (using Spring Data JPA), email services, and other third-party integrations.
*   **Presentation Layer:** The outermost layer, containing the REST API controllers. It handles HTTP requests and responses and communicates with the Application Layer.

### 2.2. SOLID Principles

The implementation will adhere to the SOLID principles to create a system that is robust and easy to maintain.

### 2.3. API Design

The service will expose a **stateless RESTful API**. Authentication will be managed via JSON Web Tokens (JWTs) delivered in secure, HTTP-only cookies, making it suitable for consumption by a wide range of clients (web SPAs, mobile apps, desktop apps) without being vulnerable to common web attacks like CSRF.

---

## 3. Core Technologies & Rationale

| Technology | Choice | Rationale |
| :--- | :--- | :--- |
| **Language** | Kotlin | A modern, concise, and null-safe JVM language that is fully interoperable with Java and is the official language for Android development. Its expressive syntax reduces boilerplate and improves developer productivity. |
| **Framework** | Spring Boot 3 | Provides a robust, auto-configured, and highly scalable foundation for building enterprise-grade RESTful services. |
| **Build Tool** | Gradle (Kotlin DSL) | A powerful and flexible build tool that offers a more expressive and maintainable build script than traditional XML-based tools, especially with the Kotlin DSL. |
| **Database** | PostgreSQL | A powerful, open-source object-relational database system with a strong reputation for reliability, feature robustness, and performance. |
| **Security** | Spring Security 6 | The industry standard for securing Spring applications, providing comprehensive and customizable authentication and authorization mechanisms. |
| **Data Access** | Spring Data JPA | Simplifies data access by providing a repository abstraction layer, reducing the need for boilerplate data access code. |
| **Migrations** | Liquibase | Ensures that database schema changes are version-controlled, repeatable, and automated, which is critical for CI/CD pipelines. |
| **Email** | JavaMailSender & Thymeleaf | A standard combination for sending rich, styled HTML emails for user communication like verification and password resets. |

---

## 4. Proposed Project Structure (Clean Architecture)

This structure separates the project into the distinct architectural layers.

```
.
├── gradle/                         // Gradle Wrapper scripts
├── src/
│   ├── main/
│   │   ├── kotlin/
│   │   │   └── com/febin/evangelist/
│   │   │       ├── EvangelistApplication.kt  // Main Spring Boot application class
│   │   │       ├── domain/                   // Core business logic and models
│   │   │       │   ├── model/              // E.g., User.kt, Role.kt, AccountStatus.kt
│   │   │       │   └── repository/         // Repository INTERFACES (defined in the domain)
│   │   │       ├── application/              // Use cases of the application
│   │   │       │   ├── service/            // Service INTERFACES (e.g., UserService.kt)
│   │   │       │   └── dto/                // Data Transfer Objects for use cases
│   │   │       ├── infrastructure/           // Implementation of external concerns
│   │   │       │   ├── config/             // E.g., SecurityConfig.kt
│   │   │       │   ├── db/                 // Database-specific implementations
│   │   │       │   │   └── changelog/      // Liquibase XML changelog files
│   │   │       │   └── email/              // EmailService implementation
│   │   │       └── presentation/             // The API layer
│   │   │           └── controller/         // REST API controllers
│   │   └── resources/
│   │       ├── application.properties      // Main application configuration
│   │       └── templates/                  // HTML email templates
│   └── test/
│       └── kotlin/
├── .gitignore
├── build.gradle.kts                // The primary Gradle build script
├── gradlew & gradlew.bat           // Gradle Wrapper execution scripts
└── settings.gradle.kts             // Gradle settings file
```

---

## 5. API Endpoint Definitions

### 5.1. Authentication Endpoints

| Endpoint | Method | Description |
| :--- | :--- | :--- |
| `/api/auth/signup` | `POST` | Registers a new user. The account will be `UNVERIFIED`. |
| `/api/auth/verify` | `GET` | Verifies a user's account using a code sent via email. |
| `/api/auth/login` | `POST` | Authenticates a user and returns secure session cookies. |
| `/api/auth/refresh` | `POST` | Refreshes the access token using the `refresh_token` cookie. |
| `/api/auth/logout` | `POST` | Logs out the user by invalidating and clearing session cookies. |

### 5.1.1. Social Authentication

In addition to local authentication, the service supports social login with Google.

| Provider | Endpoint | Method | Description |
| :--- | :--- | :--- | :--- |
| Google | `/oauth2/authorization/google` | `GET` | Initiates the Google OAuth2 login flow. This is a browser-only endpoint. |

### 5.2. Password Management Endpoints

| Endpoint | Method | Description |
| :--- | :--- | :--- |
| `/api/auth/password/forgot` | `POST` | Initiates the password reset process by sending a 6-digit code to the user's email. |
| `/api/auth/password/reset` | `POST` | Resets the user's password using the code from the email. |

### 5.3. User Account Management Endpoints

| Endpoint | Method | Description |
| :--- | :--- | :--- |
| `/api/users/me` | `GET` | **(Authenticated)** Retrieves the profile of the currently logged-in user. |
| `/api/users/me/password` | `POST` | **(Authenticated)** Allows the logged-in user to change their own password. |
| `/api/users/me` | `DELETE` | **(Authenticated)** Permanently deletes the currently logged-in user's account. |

### 5.4. Admin Endpoints

| Endpoint | Method | Description |
| :--- | :--- | :--- |
| `/api/admin/users` | `GET` | **(Admin Only)** Retrieves a list of all users. |
| `/api/admin/users/{id}` | `GET` | **(Admin Only)** Retrieves the details of a specific user by their ID. |
| `/api/admin/roles` | `GET` | **(Admin Only)** Retrieves a list of all available roles. |
| `/api/admin/users/{id}/roles` | `PUT` | **(Admin Only)** Updates the roles for a specific user. |
| `/api/admin/users/{id}/disable` | `POST` | **(Admin Only)** Disables a user's account. |
| `/api/admin/users/{id}/enable` | `POST` | **(Admin Only)** Enables a previously disabled user's account. |
| `/api/admin/users/{id}/delete` | `DELETE` | **(Admin Only)** Permanently deletes a user and all their related data. |

---

## 6. Key Architectural Flows

This section explains how the different architectural layers collaborate to fulfill a request.

### Example Flow: User Registration

1.  **Presentation Layer:**
    *   A `POST` request hits the `AuthController` at the `/api/auth/signup` endpoint.
    *   The controller validates the incoming `SignupRequest` DTO.
    *   It calls the `registerUser` method on the `AuthService` interface (from the Application Layer).

2.  **Application Layer:**
    *   The `AuthServiceImpl` (the implementation of the service interface) receives the call.
    *   It orchestrates the use case: it calls the `UserRepository` interface (from the Domain Layer) to check if the user already exists.
    *   It creates a `User` domain object.
    *   It uses the `PasswordEncoder` (an infrastructure component injected via an interface) to hash the password.
    *   It calls the `UserRepository` interface again to save the new `User`.
    *   It calls the `EmailService` interface to send the verification email.

3.  **Infrastructure Layer:**
    *   The database repository (a Spring Data JPA implementation of the `UserRepository` interface) translates the service call into a SQL `INSERT` statement and executes it against the PostgreSQL database.
    *   The `SmtpEmailService` (the implementation of the `EmailService` interface) uses `JavaMailSender` to send the email.

This strict separation ensures that the core logic in the Application Layer is completely independent of whether the database is PostgreSQL or MySQL, or whether emails are sent via SMTP or another service.

---

## 7. Setup and Deployment

### 7.1. Prerequisites
*   JDK 21 or higher
*   A running PostgreSQL instance

### 7.2. Running the Application
1.  Create the PostgreSQL database: `CREATE DATABASE authdb;`
2.  Update the `spring.datasource.*` properties in `application.properties` with your PostgreSQL connection details.
3.  Run the application using the Gradle wrapper command: `./gradlew bootRun`.
