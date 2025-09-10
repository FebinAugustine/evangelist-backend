package com.febin.evangelist.application.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

/**
 * Data Transfer Object for the forgot password request.
 */
data class ForgotPasswordRequest(
    @field:NotBlank(message = "Email is required.")
    @field:Email(message = "Email should be valid.")
    val email: String
)
