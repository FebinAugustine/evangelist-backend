package com.febin.evangelist.application.service

import com.febin.evangelist.domain.model.RefreshToken
import com.febin.evangelist.domain.model.User
import java.util.Optional

/**
 * Defines the contract for operations related to refresh tokens.
 */
interface RefreshTokenService {
    fun findByToken(token: String): Optional<RefreshToken>
    fun createRefreshToken(user: User): RefreshToken
    fun verifyExpiration(token: RefreshToken): RefreshToken
    fun deleteByUserId(userId: Long)
}
