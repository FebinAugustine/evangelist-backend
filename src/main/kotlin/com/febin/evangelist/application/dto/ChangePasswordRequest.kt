package com.febin.evangelist.application.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

/**
 * Data Transfer Object for changing a user's password.
 */
data class ChangePasswordRequest(
    @field:NotBlank(message = "Current password is required.")
    val currentPassword: String,

    @field:NotBlank(message = "New password is required.")
    @field:Size(min = 8, message = "New password must be at least 8 characters long.")
    val newPassword: String
)
