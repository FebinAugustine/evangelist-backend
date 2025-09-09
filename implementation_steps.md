# Prompt: Step-by-Step Implementation Guide for Kotlin Auth Service

**Objective:** To build a secure, production-ready, and maintainable authentication and authorization microservice using Kotlin, Spring Boot, Gradle, and PostgreSQL, following the principles of Clean Architecture and SOLID design.

**Audience:** A mid-level software developer with a basic understanding of Spring Boot.

---

### Step 0: Project Setup with Gradle

1.  **Initialize a new Spring Boot project** using the Spring Initializr (`start.spring.io`).
2.  **Select the following options:**
    *   **Project:** Gradle - Kotlin
    *   **Language:** Kotlin
    *   **Spring Boot:** 3.x.x
    *   **Packaging:** Jar
    *   **Java:** 21
3.  **Add the following dependencies:**
    *   Spring Web
    *   Spring Security
    *   Spring Data JPA
    *   PostgreSQL Driver
    *   Flyway Migration
    *   Validation
    *   Spring Boot Starter Mail
    *   Spring Boot Starter Thymeleaf
    *   JSON Web Token (jjwt-api, jjwt-impl, jjwt-jackson)
4.  **Generate and open the project** in your IDE.

---

### Step 1: Configure the Core Environment

1.  **Configure `application.properties`:**
    *   Set up the `spring.datasource.*` properties to connect to your local PostgreSQL database (`authdb`).
    *   Configure `spring.jpa.hibernate.ddl-auto` to `validate` to ensure Hibernate checks the schema against Flyway migrations.
    *   Set up the `spring.mail.*` properties with your SMTP server details (e.g., Gmail with an App Password).
    *   Define custom properties for JWT secrets and application URLs (`jwt.secret`, `app.base-url`, etc.).
2.  **Set up the Database:**
    *   Create the initial PostgreSQL database: `CREATE DATABASE authdb;`

---

### Step 2: Establish the Domain Layer (The Core)

1.  **Create Core Entities:**
    *   In the `domain.model` package, create the following Kotlin data classes as JPA entities:
        *   `User.kt`: Must implement Spring Security's `UserDetails`.
        *   `Role.kt`
        *   `AccountStatus.kt` (as an enum: `UNVERIFIED`, `ACTIVE`, `DISABLED`).
        *   `UserProvider.kt`
        *   `RefreshToken.kt`
2.  **Define Repository Interfaces:**
    *   In the `domain.repository` package, create the repository **interfaces**.
    *   `UserRepository.kt`: Must extend `JpaRepository` and include methods like `findByEmail`, `findByVerificationCode`, etc.
    *   `RoleRepository.kt`: Must extend `JpaRepository` and include `findByNameIn`.
    *   `RefreshTokenRepository.kt`, `UserProviderRepository.kt`.

---

### Step 3: Implement the Infrastructure Layer

1.  **Implement Repositories:**
    *   In the `infrastructure.db.repository` package, create the Spring Data JPA implementations of the repository interfaces defined in the domain layer.
2.  **Create Flyway Migrations:**
    *   In `src/main/resources/db/migration`, create the necessary SQL migration scripts (`V1__...`, `V2__...`, etc.) to create all the required tables (`users`, `roles`, `user_roles`, etc.) for your PostgreSQL database.
3.  **Implement the Email Service:**
    *   In the `infrastructure.email` package, create an `EmailServiceImpl.kt` that implements the `EmailService` interface from the application layer. This class will use `JavaMailSender` and `Thymeleaf` to send styled HTML emails.

---

### Step 4: Implement the Application Layer (The Use Cases)

1.  **Define Service Interfaces:**
    *   In the `application.service` package, define the service interfaces that represent the application's use cases (e.g., `UserService.kt`, `AuthService.kt`).
2.  **Create DTOs:**
    *   In the `application.dto` package, create all necessary Data Transfer Objects for handling API requests and responses (e.g., `LoginRequest.kt`, `UserResponse.kt`, `PasswordResetRequest.kt`).
3.  **Implement Services:**
    *   In a sub-package of `application.service`, create the implementations of your service interfaces (e.g., `UserServiceImpl.kt`).
    *   This is where the core business logic resides: registering users, verifying codes, linking accounts, etc. These services will depend on the repository **interfaces** from the domain layer.

---

### Step 5: Build the Presentation Layer (The API)

1.  **Create Controllers:**
    *   In the `presentation.controller` package, create your REST controllers (`AuthController`, `UserController`, `AdminController`, etc.).
2.  **Define Endpoints:**
    *   Implement all the API endpoints as defined in the PRD. These endpoints should be responsible for receiving HTTP requests, validating the input (DTOs), calling the appropriate methods on the service interfaces, and returning the correct HTTP response.
3.  **Implement Exception Handling:**
    *   Create a `GlobalExceptionHandler.kt` with the `@RestControllerAdvice` annotation to handle custom exceptions (`InvalidTokenException`, etc.) and return clean, user-friendly JSON error messages.

---

### Step 6: Configure Security

1.  **Implement `SecurityConfig.kt`:**
    *   Create the main security configuration file.
    *   Define two `SecurityFilterChain` beans with `@Order` to handle API and web traffic separately.
    *   **API Chain (`@Order(1)`):** Configure it to be stateless, disable CSRF, and set up a custom `AuthenticationEntryPoint` to return `401` errors for unauthenticated API requests.
    *   **Web Chain (`@Order(2)`):** Configure it for stateful, browser-based flows like OAuth2, with CSRF protection enabled.
2.  **Implement `JwtAuthenticationFilter.kt`:**
    *   Create the custom JWT filter that extracts the token from the cookie, validates it, and uses the `UserDetailsService` to set the `Authentication` in the security context.
3.  **Implement OAuth2 Handlers:**
    *   Create the `CustomOAuth2UserService.kt` and the success/failure handlers for the OAuth2 login flow.

---

### Step 7: Final Touches & Verification

1.  **Update Documentation:** Ensure the `README.md` and `documentation.md` files are updated to reflect the final implementation.
2.  **Test All Endpoints:** Use a tool like Postman to thoroughly test every API endpoint, including all success and failure cases (e.g., wrong password, invalid token, insufficient permissions).
3.  **Review and Refactor:** Review the entire codebase for adherence to Clean Architecture and SOLID principles. Refactor where necessary to improve clarity, maintainability, and security.

