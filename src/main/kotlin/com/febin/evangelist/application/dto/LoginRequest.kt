package com.febin.evangelist.application.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

/**
 * Data Transfer Object for user login requests.
 */
data class LoginRequest(
    @field:NotBlank(message = "Email is required.")
    @field:Email(message = "Email should be valid.")
    val email: String,

    @field:NotBlank(message = "Password is required.")
    val password: String
)
