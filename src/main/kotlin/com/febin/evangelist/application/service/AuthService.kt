package com.febin.evangelist.application.service

import com.febin.evangelist.application.dto.LoginRequest
import com.febin.evangelist.application.dto.MessageResponse
import com.febin.evangelist.application.dto.SignupRequest
import com.febin.evangelist.domain.model.User
import org.springframework.http.ResponseEntity

/**
 * Defines the contract for authentication-related operations.
 */
interface AuthService {
    /**
     * Registers a new user.
     *
     * @param signupRequest The request containing user details.
     * @return A message response indicating success.
     */
    fun registerUser(signupRequest: SignupRequest): MessageResponse

    /**
     * Verifies a user's account using a verification code.
     *
     * @param verificationCode The code sent to the user's email.
     * @return A message response indicating success.
     */
    fun verifyUser(verificationCode: String): MessageResponse

    /**
     * Authenticates a user and returns JWTs in secure cookies.
     *
     * @param loginRequest The request containing login credentials.
     * @return A response entity containing a success message.
     */
    fun authenticateUser(loginRequest: LoginRequest): ResponseEntity<MessageResponse>
}
