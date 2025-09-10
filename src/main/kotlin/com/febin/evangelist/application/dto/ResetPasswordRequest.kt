package com.febin.evangelist.application.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

/**
 * Data Transfer Object for the reset password request.
 */
data class ResetPasswordRequest(
    @field:NotBlank(message = "Token is required.")
    val token: String,

    @field:NotBlank(message = "New password is required.")
    @field:Size(min = 8, message = "New password must be at least 8 characters long.")
    val newPassword: String
)
