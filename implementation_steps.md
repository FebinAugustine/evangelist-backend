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
    *   Spring Boot Starter OAuth2 Client
    *   Spring Data JPA
    *   PostgreSQL Driver
    *   Liquibase Migration
    *   Validation
    *   Spring Boot Starter Mail
    *   Spring Boot Starter Thymeleaf
    *   JSON Web Token (jjwt-api, jjwt-impl, jjwt-jackson)
    *   Bucket4j (for rate limiting)
4.  **Add the `kotlin-jpa` plugin** to your `build.gradle.kts` file's `plugins` block. This is crucial for generating no-argument constructors for your JPA entities.

    ```kotlin
    plugins {
        // ... other plugins
        alias(libs.plugins.kotlin.jpa)
    }
    ```

---

### Step 1: Configure the Core Environment

1.  **Configure `application.properties`:**
    *   Set up the `spring.datasource.*` properties to connect to your local PostgreSQL database (`authdb`).
    *   Configure `spring.jpa.hibernate.ddl-auto` to `validate` to ensure Hibernate checks the schema against Liquibase migrations.
    *   Set up the `spring.liquibase.change-log` property to point to your master changelog file (e.g., `classpath:/db/changelog/db.changelog-master.xml`).
    *   Set up the `spring.mail.*` properties with your SMTP server details. Use an environment variable for the password (e.g., `spring.mail.password=${SPRING_MAIL_PASSWORD}`).
    *   Define custom properties for JWT secrets. The secret **must** be a valid, standard Base64-encoded string of at least 256 bits.
2.  **Set up the Database:**
    *   Create the initial PostgreSQL database: `CREATE DATABASE authdb;`

---

### Step 2: Establish the Domain Layer (The Core)

1.  **Create Core Entities:**
    *   In the `domain.model` package, create the following Kotlin classes as JPA entities. **Important:** Use a regular `class`, not a `data class`, for entities to avoid JVM signature clashes.
        *   `User.kt`: Must implement `UserDetails` and `OAuth2User`. Use private properties (e.g., `_email`, `_password`) and public getters to satisfy the interfaces. Add fields for verification and password reset tokens.
        *   `Role.kt`
        *   `AccountStatus.kt` (as an enum: `UNVERIFIED`, `ACTIVE`, `DISABLED`).
        *   `UserProvider.kt` (as an enum: `LOCAL`, `GOOGLE`, `GITHUB`).
        *   `RefreshToken.kt`
2.  **Define Repository Interfaces:**
    *   In the `domain.repository` package, create the repository **interfaces**.
    *   `UserRepository.kt`: Must extend `JpaRepository` and include methods like `findBy_email`, `existsBy_email`, `findByVerificationCode`, and `findByPasswordResetToken`.
    *   `RoleRepository.kt`: Must extend `JpaRepository`.
    *   `RefreshTokenRepository.kt`: Must extend `JpaRepository`.

---

### Step 3: Implement the Infrastructure Layer

1.  **Create Liquibase Migrations:**
    *   In `src/main/resources/db/changelog`, create a master changelog file (`db.changelog-master.xml`).
    *   Create individual XML changelog files for each schema change (e.g., `001-create-tables.xml`, `002-insert-roles.xml`).
    *   Define your tables, columns, and constraints using Liquibase's XML format.
2.  **Implement the Email Service:**
    *   In the `infrastructure.email` package, create an `EmailServiceImpl.kt` that implements the `EmailService` interface. This class will use `JavaMailSender` and `Thymeleaf` to send styled HTML emails for both account verification and password resets.
3.  **Implement Rate Limiting:**
    *   Create a `RateLimitingInterceptor` that uses Bucket4j to track and limit requests by IP address.
    *   Create a `WebConfig` class that implements `WebMvcConfigurer` to register the interceptor and apply it to your API endpoints.

---

### Step 4: Implement the Application Layer (The Use Cases)

1.  **Define Service Interfaces:**
    *   In the `application.service` package, define the service interfaces: `AuthService`, `UserService`, `AdminService`, and `RefreshTokenService`.
2.  **Create DTOs:**
    *   In the `application.dto` package, create all necessary Data Transfer Objects: `SignupRequest`, `LoginRequest`, `ForgotPasswordRequest`, `ResetPasswordRequest`, `ChangePasswordRequest`, `UpdateUserRolesRequest`, `UserResponse`, and `MessageResponse`.
3.  **Implement Services:**
    *   Implement the service classes (`...Impl`). This is where the core business logic resides: registering users, handling logins, processing password resets, managing user accounts, and performing administrative actions.

---

### Step 5: Build the Presentation Layer (The API)

1.  **Create Controllers:**
    *   In the `presentation.controller` package, create your REST controllers: `AuthController`, `UserController`, `AdminController`, and `HealthCheckController`.
2.  **Define Endpoints:**
    *   Implement all the API endpoints as defined in the PRD, including the new endpoints for password management, user self-service, and administration.
3.  **Implement Exception Handling:**
    *   Create a `GlobalExceptionHandler.kt` with the `@RestControllerAdvice` annotation to handle exceptions and return clean JSON error messages.
    *   Create a `CustomAuthenticationEntryPoint.kt` to handle unauthenticated requests to protected API endpoints, ensuring it returns a `401 Unauthorized` status with a JSON error message instead of an HTML login page.

---

### Step 6: Configure Security

1.  **Implement `SecurityConfig.kt`:**
    *   Create the main security configuration file.
    *   Define two `SecurityFilterChain` beans with `@Order` to handle API and web traffic separately.
    *   **API Chain (`@Order(1)`):** Configure it to be stateless, disable CSRF, and set up the `CustomAuthenticationEntryPoint`.
    *   **Web Chain (`@Order(2)`):** Configure it for stateful, browser-based flows like OAuth2.
2.  **Implement `JwtAuthenticationFilter.kt`:**
    *   Create the custom JWT filter that extracts the token from the cookie, validates it, and uses the `UserDetailsService` to set the `Authentication` in the security context.
3.  **Implement OAuth2 Handlers:**
    *   Create the `CustomOAuth2UserService` and the `OAuth2AuthenticationSuccessHandler` and `OAuth2AuthenticationFailureHandler` for the OAuth2 login flow.
4.  **Google OAuth2 Login Endpoint:**
    *   To initiate the Google OAuth2 login flow, direct the user's browser to the following endpoint: `/oauth2/authorization/google`. Spring Security will then automatically redirect the user to Google's authentication page.

---

### Step 7: Final Touches & Verification

1.  **Update Documentation:** Ensure the `README.md` and `Kotlin-SB-Grad.md` files are updated to reflect the final implementation.
2.  **Test All Endpoints:** Use a tool like Postman to thoroughly test every API endpoint, including all success and failure cases.
3.  **Review and Refactor:** Review the entire codebase for adherence to Clean Architecture and SOLID principles. Refactor where necessary to improve clarity, maintainability, and security.
