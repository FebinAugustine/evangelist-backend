package com.febin.evangelist.application.service.impl

import com.febin.evangelist.application.dto.ForgotPasswordRequest
import com.febin.evangelist.application.dto.LoginRequest
import com.febin.evangelist.application.dto.MessageResponse
import com.febin.evangelist.application.dto.ResetPasswordRequest
import com.febin.evangelist.application.dto.SignupRequest
import com.febin.evangelist.application.service.AuthService
import com.febin.evangelist.application.service.EmailService
import com.febin.evangelist.application.service.RefreshTokenService
import com.febin.evangelist.domain.model.AccountStatus
import com.febin.evangelist.domain.model.User
import com.febin.evangelist.domain.repository.RefreshTokenRepository
import com.febin.evangelist.domain.repository.RoleRepository
import com.febin.evangelist.domain.repository.UserRepository
import com.febin.evangelist.infrastructure.security.JwtTokenProvider
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.*

@Service
class AuthServiceImpl(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val passwordEncoder: PasswordEncoder,
    private val authenticationManager: AuthenticationManager,
    private val jwtTokenProvider: JwtTokenProvider,
    private val emailService: EmailService,
    private val refreshTokenService: RefreshTokenService,
    private val refreshTokenRepository: RefreshTokenRepository // Injected for direct deletion
) : AuthService {

    override fun registerUser(signupRequest: SignupRequest): MessageResponse {
        if (userRepository.existsBy_email(signupRequest.email)) {
            throw RuntimeException("Error: Email is already in use!") // Replace with custom exception
        }

        val user = User(
            _email = signupRequest.email,
            _password = passwordEncoder.encode(signupRequest.password),
            verificationCode = UUID.randomUUID().toString()
        )

        val userRole = roleRepository.findByNameIn(listOf("ROLE_USER")).firstOrNull()
            ?: throw RuntimeException("Error: Role is not found.")

        user.roles = mutableSetOf(userRole)
        userRepository.save(user)

        emailService.sendVerificationEmail(user)

        return MessageResponse("User registered successfully! Please check your email for verification.")
    }

    override fun verifyUser(verificationCode: String): MessageResponse {
        val user = userRepository.findByVerificationCode(verificationCode).orElseThrow {
            RuntimeException("Error: Invalid verification code.") // Replace with custom exception
        }

        user.status = AccountStatus.ACTIVE
        user.verificationCode = null
        userRepository.save(user)

        return MessageResponse("Account verified successfully!")
    }

    override fun authenticateUser(loginRequest: LoginRequest): ResponseEntity<MessageResponse> {
        // Check if user exists before attempting authentication
        if (!userRepository.existsBy_email(loginRequest.email)) {
            throw RuntimeException("No user exist with this credentials")
        }

        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(loginRequest.email, loginRequest.password)
        )

        SecurityContextHolder.getContext().authentication = authentication

        val user = authentication.principal as User
        val accessToken = jwtTokenProvider.generateToken(user.username)
        val refreshToken = refreshTokenService.createRefreshToken(user)

        val accessTokenCookie = ResponseCookie.from("access_token", accessToken)
            .httpOnly(true)
            .secure(true) // Set to true in production
            .path("/")
            .maxAge(60 * 60) // 1 hour
            .build()

        val refreshTokenCookie = ResponseCookie.from("refresh_token", refreshToken.token)
            .httpOnly(true)
            .secure(true) // Set to true in production
            .path("/")
            .maxAge(7 * 24 * 60 * 60) // 7 days
            .build()

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
            .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
            .body(MessageResponse("User authenticated successfully!"))
    }

    override fun refreshToken(refreshToken: String): ResponseEntity<MessageResponse> {
        return refreshTokenService.findByToken(refreshToken)
            .map { refreshTokenService.verifyExpiration(it) }
            .map { it.user }
            .map { user ->
                val accessToken = jwtTokenProvider.generateToken(user.username)
                val accessTokenCookie = ResponseCookie.from("access_token", accessToken)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(60 * 60) // 1 hour
                    .build()

                ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                    .body(MessageResponse("Access token refreshed successfully!"))
            }
            .orElseThrow { RuntimeException("$refreshToken Refresh token is not in database!") } // Replace with custom exception
    }

    @Transactional
    override fun logout(refreshToken: String?): ResponseEntity<MessageResponse> {
        if (refreshToken != null) {
            refreshTokenRepository.findByToken(refreshToken).ifPresent { token ->
                refreshTokenRepository.delete(token)
            }
        }

        val accessTokenCookie = ResponseCookie.from("access_token", "")
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(0)
            .build()

        val refreshTokenCookie = ResponseCookie.from("refresh_token", "")
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(0)
            .build()

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
            .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
            .body(MessageResponse("You've been logged out successfully!"))
    }

    @Transactional
    override fun forgotPassword(forgotPasswordRequest: ForgotPasswordRequest): MessageResponse {
        val userOptional = userRepository.findBy_email(forgotPasswordRequest.email)

        if (userOptional.isPresent) {
            val user = userOptional.get()
            // Generate a 6-digit code
            user.passwordResetToken = (100000..999999).random().toString()
            user.passwordResetTokenExpiry = Instant.now().plusSeconds(3600) // 1 hour expiry
            userRepository.save(user)
            emailService.sendPasswordResetEmail(user)
        }
        // For security, always return a success message, even if the user does not exist.
        return MessageResponse("If an account with that email exists, a password reset link has been sent.")
    }

    @Transactional
    override fun resetPassword(resetPasswordRequest: ResetPasswordRequest): MessageResponse {
        val user = userRepository.findByPasswordResetToken(resetPasswordRequest.token).orElseThrow {
            RuntimeException("Error: Invalid password reset token.") // Replace with custom exception
        }

        if (user.passwordResetTokenExpiry?.isBefore(Instant.now()) == true) {
            throw RuntimeException("Error: Password reset token has expired.") // Replace with custom exception
        }

        user.updatePassword(passwordEncoder.encode(resetPasswordRequest.newPassword))
        user.passwordResetToken = null
        user.passwordResetTokenExpiry = null
        userRepository.save(user)

        return MessageResponse("Password has been reset successfully.")
    }
}
