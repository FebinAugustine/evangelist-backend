package com.febin.evangelist.application.service.impl

import com.febin.evangelist.application.dto.LoginRequest
import com.febin.evangelist.application.dto.MessageResponse
import com.febin.evangelist.application.dto.SignupRequest
import com.febin.evangelist.application.service.AuthService
import com.febin.evangelist.application.service.EmailService
import com.febin.evangelist.application.service.RefreshTokenService
import com.febin.evangelist.domain.model.AccountStatus
import com.febin.evangelist.domain.model.User
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
import java.util.*

@Service
class AuthServiceImpl(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val passwordEncoder: PasswordEncoder,
    private val authenticationManager: AuthenticationManager,
    private val jwtTokenProvider: JwtTokenProvider,
    private val emailService: EmailService,
    private val refreshTokenService: RefreshTokenService
) : AuthService {

    override fun registerUser(signupRequest: SignupRequest): MessageResponse {
        if (userRepository.existsByEmail(signupRequest.email)) {
            throw RuntimeException("Error: Email is already in use!") // Replace with custom exception
        }

        val user = User(
            email = signupRequest.email,
            password = passwordEncoder.encode(signupRequest.password),
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
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(loginRequest.email, loginRequest.password)
        )

        SecurityContextHolder.getContext().authentication = authentication

        val user = authentication.principal as User
        val accessToken = jwtTokenProvider.generateToken(user.email)
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
}
