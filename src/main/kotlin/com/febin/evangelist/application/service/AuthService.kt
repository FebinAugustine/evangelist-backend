package com.febin.evangelist.application.service

import com.febin.evangelist.application.dto.ForgotPasswordRequest
import com.febin.evangelist.application.dto.LoginRequest
import com.febin.evangelist.application.dto.MessageResponse
import com.febin.evangelist.application.dto.ResetPasswordRequest
import com.febin.evangelist.application.dto.SignupRequest
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

    /**
     * Refreshes an access token using a refresh token.
     *
     * @param refreshToken The refresh token string from the cookie.
     * @return A response entity containing a new access token cookie.
     */
    fun refreshToken(refreshToken: String): ResponseEntity<MessageResponse>

    /**
     * Logs out a user by clearing their session cookies.
     *
     * @param refreshToken The refresh token string from the cookie, which may be null.
     * @return A response entity containing a success message.
     */
    fun logout(refreshToken: String?): ResponseEntity<MessageResponse>

    /**
     * Initiates the password reset process for a user.
     *
     * @param forgotPasswordRequest The request containing the user's email.
     * @return A message response indicating success.
     */
    fun forgotPassword(forgotPasswordRequest: ForgotPasswordRequest): MessageResponse

    /**
     * Resets a user's password using a valid reset token.
     *
     * @param resetPasswordRequest The request containing the token and new password.
     * @return A message response indicating success.
     */
    fun resetPassword(resetPasswordRequest: ResetPasswordRequest): MessageResponse
}
