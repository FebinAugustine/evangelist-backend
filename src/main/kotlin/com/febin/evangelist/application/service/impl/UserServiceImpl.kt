package com.febin.evangelist.application.service.impl

import com.febin.evangelist.application.dto.ChangePasswordRequest
import com.febin.evangelist.application.dto.MessageResponse
import com.febin.evangelist.application.dto.UserResponse
import com.febin.evangelist.application.service.RefreshTokenService
import com.febin.evangelist.application.service.UserService
import com.febin.evangelist.domain.model.User
import com.febin.evangelist.domain.repository.UserRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val refreshTokenService: RefreshTokenService,
    private val passwordEncoder: PasswordEncoder // Injected PasswordEncoder
) : UserService {

    override fun getCurrentUser(): UserResponse {
        val user = getCurrentAuthenticatedUser()
        return mapUserToResponse(user)
    }

    override fun deleteCurrentUser(): MessageResponse {
        val user = getCurrentAuthenticatedUser()
        refreshTokenService.deleteByUserId(user.id)
        userRepository.delete(user)
        return MessageResponse("User account deleted successfully.")
    }

    override fun changePassword(request: ChangePasswordRequest): MessageResponse {
        val user = getCurrentAuthenticatedUser()

        // Verify the current password
        if (!passwordEncoder.matches(request.currentPassword, user.password)) {
            throw RuntimeException("Error: Incorrect current password.") // Replace with custom exception
        }

        // Encode and set the new password using the correct method
        user.updatePassword(passwordEncoder.encode(request.newPassword))
        userRepository.save(user)

        return MessageResponse("Password changed successfully.")
    }

    private fun getCurrentAuthenticatedUser(): User {
        val principal = SecurityContextHolder.getContext().authentication.principal
        val email = if (principal is User) {
            principal.username // This is correct because UserDetails.getUsername() returns the email
        } else {
            principal.toString()
        }
        return userRepository.findBy_email(email).orElseThrow {
            RuntimeException("Error: Authenticated user not found in database.") // Replace with custom exception
        }
    }

    private fun mapUserToResponse(user: User): UserResponse {
        return UserResponse(
            id = user.id,
            email = user.username, // Corrected to use the public getter
            status = user.status,
            provider = user.provider,
            roles = user.roles.map { it.name }.toSet()
        )
    }
}
