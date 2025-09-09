package com.febin.evangelist.application.dto

/**
 * A generic DTO for returning structured error messages.
 */
data class ErrorResponse(
    val statusCode: Int,
    val message: String,
    val description: String? = null
)
