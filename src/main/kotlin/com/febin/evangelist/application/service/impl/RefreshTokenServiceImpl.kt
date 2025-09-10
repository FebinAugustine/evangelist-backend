package com.febin.evangelist.application.service.impl

import com.febin.evangelist.application.service.RefreshTokenService
import com.febin.evangelist.domain.model.RefreshToken
import com.febin.evangelist.domain.model.User
import com.febin.evangelist.domain.repository.RefreshTokenRepository
import com.febin.evangelist.domain.repository.UserRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.*

@Service
class RefreshTokenServiceImpl(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val userRepository: UserRepository,
    @Value("\${jwt.refresh-token.expiration-ms}") private val refreshTokenExpirationMs: Long
) : RefreshTokenService {

    override fun findByToken(token: String): Optional<RefreshToken> {
        return refreshTokenRepository.findByToken(token)
    }

    @Transactional
    override fun createRefreshToken(user: User): RefreshToken {
        // First, delete any existing token for this user to prevent duplicates.
        refreshTokenRepository.deleteByUser(user)

        // Then, create and save the new token.
        val refreshToken = RefreshToken(
            user = user,
            token = UUID.randomUUID().toString(),
            expiryDate = Instant.now().plusMillis(refreshTokenExpirationMs)
        )
        return refreshTokenRepository.save(refreshToken)
    }

    override fun verifyExpiration(token: RefreshToken): RefreshToken {
        if (token.expiryDate.isBefore(Instant.now())) {
            refreshTokenRepository.delete(token)
            throw RuntimeException("\${token.token} Refresh token was expired. Please make a new signin request") // Replace with custom exception
        }
        return token
    }

    @Transactional
    override fun deleteByUserId(userId: Long) {
        val user = userRepository.findById(userId).orElseThrow {
            RuntimeException("User not found with id: $userId") // Replace with custom exception
        }
        refreshTokenRepository.deleteByUser(user)
    }
}
