package com.febin.evangelist.application.dto

import com.febin.evangelist.domain.model.AccountStatus
import com.febin.evangelist.domain.model.UserProvider

/**
 * Data Transfer Object for returning user information.
 */
data class UserResponse(
    val id: Long,
    val email: String,
    val status: AccountStatus,
    val provider: UserProvider,
    val roles: Set<String>
)
