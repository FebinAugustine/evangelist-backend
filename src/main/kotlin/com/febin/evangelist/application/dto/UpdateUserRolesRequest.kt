package com.febin.evangelist.application.dto

import jakarta.validation.constraints.NotEmpty

/**
 * Data Transfer Object for updating a user's roles.
 */
data class UpdateUserRolesRequest(
    @field:NotEmpty(message = "Roles cannot be empty.")
    val roles: Set<String>
)
