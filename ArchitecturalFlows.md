# Key Architectural Flows in the Kotlin Auth Service

This document details the key architectural flows of the authentication service, illustrating how the Presentation, Application, and Infrastructure layers collaborate to fulfill user requests. Each flow follows the principles of Clean Architecture, ensuring a separation of concerns and a maintainable codebase.

---

## 1. User Registration Flow

This flow describes the process of a new user signing up for the service.

1.  **Presentation Layer:**
    *   A `POST` request containing the user's details hits the `AuthController` at the `/api/auth/signup` endpoint.
    *   The controller validates the incoming `SignupRequest` DTO for correctness (e.g., valid email format, password length).
    *   Upon successful validation, it calls the `registerUser` method on the `AuthService` interface (from the Application Layer).

2.  **Application Layer:**
    *   The `AuthServiceImpl` receives the call.
    *   It first calls the `UserRepository` interface (from the Domain Layer) to check if a user with the given email already exists. If so, it throws an exception.
    *   It creates a new `User` domain object.
    *   It uses the `PasswordEncoder` (an infrastructure component injected via an interface) to securely hash the user's password.
    *   It generates a unique verification code for the new user.
    *   It calls the `UserRepository` interface again to save the new `User` entity to the database.
    *   Finally, it calls the `EmailService` interface to send a verification email to the user's address, containing the generated code.

3.  **Infrastructure Layer:**
    *   The `UserRepository` implementation (Spring Data JPA) translates the `save` call into a SQL `INSERT` statement and executes it against the PostgreSQL database.
    *   The `EmailServiceImpl` uses `JavaMailSender` and `Thymeleaf` to render and send the HTML verification email.

---

## 2. User Login Flow

This flow describes how a user authenticates and receives session tokens.

1.  **Presentation Layer:**
    *   A `POST` request with the user's email and password hits the `AuthController` at `/api/auth/login`.
    *   The controller validates the `LoginRequest` DTO.
    *   It calls the `loginUser` method on the `AuthService`.

2.  **Application Layer:**
    *   The `AuthServiceImpl` receives the login request.
    *   It uses the `AuthenticationManager` (a Spring Security component) to authenticate the user's credentials. The `AuthenticationManager` will internally use the `UserDetailsService` implementation to fetch the user from the database and compare the hashed passwords.
    *   Upon successful authentication, it generates a JWT access token and a refresh token.
    *   The refresh token is persisted in the database via the `RefreshTokenRepository`.
    *   The service returns the user's details along with the generated tokens.

3.  **Infrastructure Layer:**
    *   The `JwtProvider` class is used to create and sign the JWTs.
    *   The `RefreshTokenRepository` saves the refresh token to the database.
    *   The `AuthController` in the Presentation Layer receives the tokens and sets them in secure, HTTP-only cookies in the HTTP response.

---

## 3. Google OAuth2 Login Flow

This flow outlines the process for a user authenticating via their Google account.

1.  **Presentation Layer (Browser):**
    *   The user clicks a "Login with Google" button, which directs their browser to the `/oauth2/authorization/google` endpoint.
    *   Spring Security's `OAuth2AuthorizationRequestRedirectFilter` intercepts this request and redirects the user to Google's authentication page.

2.  **Google Authentication:**
    *   The user authenticates with Google and grants the application permission.
    *   Google redirects the user back to the application's configured redirect URI (e.g., `/login/oauth2/code/google`), including an authorization code.

3.  **Application & Infrastructure Layers:**
    *   The `CustomOAuth2UserService` is invoked by Spring Security.
    *   It fetches the user's details from Google using the authorization code.
    *   It checks if a user with this Google ID already exists in the database via the `UserRepository`. 
        *   If the user exists, it updates their details (e.g., name, profile picture).
        *   If the user does not exist, it creates a new `User` entity with the `UserProvider` set to `GOOGLE`.
    *   The `OAuth2AuthenticationSuccessHandler` is invoked.
    *   It generates a JWT access token and a refresh token for the user.
    *   It saves the refresh token to the database.
    *   Finally, it redirects the user's browser to a frontend URL, with the tokens included as secure cookies.

---

## 4. Token Refresh Flow

This flow explains how an expired access token can be renewed without requiring the user to log in again.

1.  **Presentation Layer:**
    *   The client makes a `POST` request to the `/api/auth/refresh` endpoint. The request includes the `refresh_token` cookie.
    *   The `AuthController` calls the `refreshToken` method on the `AuthService`.

2.  **Application Layer:**
    *   The `AuthServiceImpl` receives the refresh token.
    *   It calls the `RefreshTokenService` to find and verify the token in the database.
    *   The `RefreshTokenService` checks if the token exists and is not expired.
    *   If the token is valid, a new JWT access token is generated for the associated user.

3.  **Infrastructure Layer:**
    *   The `RefreshTokenRepository` is used to look up the refresh token.
    *   The `JwtProvider` generates the new access token.
    *   The `AuthController` sets the new access token in a secure cookie in the response.

---

## 5. Account Verification Flow

This flow describes how a user verifies their account after registration.

1.  **Presentation Layer:**
    *   The user clicks a link in their verification email, which directs them to a frontend page that makes a `GET` request to `/api/auth/verify?code={verificationCode}`.
    *   The `AuthController` receives the request and calls the `verifyAccount` method on the `AuthService`.

2.  **Application Layer:**
    *   The `AuthServiceImpl` receives the verification code.
    *   It calls the `UserRepository` to find a user by the given verification code.
    *   If a user is found, their account status is updated to `ACTIVE`, and the verification code is cleared from their record.
    *   The updated `User` entity is saved back to the database via the `UserRepository`.

3.  **Infrastructure Layer:**
    *   The `UserRepository` executes a `SELECT` query to find the user by the verification code and an `UPDATE` query to save the changes.

---

## 6. Forgot Password Flow

This flow initiates the process for a user who has forgotten their password.

1.  **Presentation Layer:**
    *   The user submits their email address to a "Forgot Password" form, which triggers a `POST` request to `/api/auth/password/forgot`.
    *   The `AuthController` validates the `ForgotPasswordRequest` DTO and calls the `forgotPassword` method on the `AuthService`.

2.  **Application Layer:**
    *   The `AuthServiceImpl` receives the email address.
    *   It calls the `UserRepository` to find the user by email.
    *   If a user exists, it generates a random, 6-digit password reset code and a corresponding expiry time.
    *   It saves the reset code and expiry time to the `User` entity.
    *   It calls the `EmailService` to send the reset code to the user's email.

3.  **Infrastructure Layer:**
    *   The `UserRepository` finds the user and saves the updated user record with the reset token.
    *   The `EmailServiceImpl` sends the email containing the reset code.

---

## 7. Reset Password Flow

This flow completes the password reset process using the code from the email.

1.  **Presentation Layer:**
    *   The user submits the reset code, their email, and their new password via a form, which triggers a `POST` request to `/api/auth/password/reset`.
    *   The `AuthController` validates the `ResetPasswordRequest` DTO and calls the `resetPassword` method on the `AuthService`.

2.  **Application Layer:**
    *   The `AuthServiceImpl` receives the request.
    *   It calls the `UserRepository` to find the user by the password reset token.
    *   It verifies that the token is not expired and matches the user.
    *   If valid, it hashes the new password using the `PasswordEncoder`.
    *   It updates the user's password and clears the reset token fields.
    *   It saves the updated `User` entity via the `UserRepository`.

3.  **Infrastructure Layer:**
    *   The `UserRepository` finds the user by the reset token and updates their password in the database.

---

## 8. Change Password Flow (Authenticated User)

This flow allows a logged-in user to change their own password.

1.  **Presentation Layer:**
    *   An authenticated user submits their old and new passwords, triggering a `POST` request to `/api/users/me/password`.
    *   The `UserController` receives the request and calls the `changePassword` method on the `UserService`.

2.  **Application Layer:**
    *   The `UserServiceImpl` gets the currently authenticated user from the `SecurityContext`.
    *   It uses the `PasswordEncoder` to verify that the provided "old password" matches the user's current password.
    *   If it matches, it hashes the "new password".
    *   It updates the `User` entity with the new hashed password and saves it via the `UserRepository`.

3.  **Infrastructure Layer:**
    *   The `UserRepository` updates the user's password in the database.

---

## 9. Logout Flow

This flow handles logging a user out and invalidating their session.

1.  **Presentation Layer:**
    *   The client sends a `POST` request to `/api/auth/logout`.
    *   The `AuthController` calls the `logoutUser` method on the `AuthService`.

2.  **Application Layer:**
    *   The `AuthServiceImpl` receives the request, which includes the `refresh_token` cookie.
    *   It calls the `RefreshTokenService` to delete the refresh token from the database, effectively invalidating the session.

3.  **Infrastructure Layer:**
    *   The `RefreshTokenRepository` deletes the specified refresh token from the database.
    *   The `AuthController` clears the access and refresh token cookies from the HTTP response, removing them from the user's browser.
