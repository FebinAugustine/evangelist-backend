package com.febin.evangelist.application.dto

import jakarta.validation.constraints.NotBlank

/**
 * Data Transfer Object for refresh token requests.
 */
data class RefreshTokenRequest(
    @field:NotBlank(message = "Refresh token is required.")
    val refreshToken: String
)
