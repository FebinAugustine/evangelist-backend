package com.febin.evangelist.application.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

/**
 * Data Transfer Object for user registration requests.
 */
data class SignupRequest(
    @field:NotBlank(message = "Email is required.")
    @field:Email(message = "Email should be valid.")
    val email: String,

    @field:NotBlank(message = "Password is required.")
    @field:Size(min = 8, message = "Password must be at least 8 characters long.")
    val password: String
)
