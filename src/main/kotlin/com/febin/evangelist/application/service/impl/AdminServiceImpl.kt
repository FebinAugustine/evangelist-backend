package com.febin.evangelist.application.service.impl

import com.febin.evangelist.application.dto.UpdateUserRolesRequest
import com.febin.evangelist.application.dto.UserResponse
import com.febin.evangelist.application.service.AdminService
import com.febin.evangelist.domain.model.User
import com.febin.evangelist.domain.repository.RoleRepository
import com.febin.evangelist.domain.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminServiceImpl(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository
) : AdminService {

    override fun getAllUsers(): List<UserResponse> {
        return userRepository.findAll().map { mapUserToResponse(it) }
    }

    override fun getUserById(id: Long): UserResponse {
        val user = userRepository.findById(id).orElseThrow {
            RuntimeException("Error: User not found.") // Replace with custom exception
        }
        return mapUserToResponse(user)
    }

    @Transactional
    override fun updateUserRoles(id: Long, request: UpdateUserRolesRequest): UserResponse {
        val user = userRepository.findById(id).orElseThrow {
            RuntimeException("Error: User not found.") // Replace with custom exception
        }

        val newRoles = roleRepository.findByNameIn(request.roles.toList()).toMutableSet()
        if (newRoles.size != request.roles.size) {
            throw RuntimeException("Error: One or more roles not found.") // Replace with custom exception
        }

        user.roles = newRoles
        val updatedUser = userRepository.save(user)

        return mapUserToResponse(updatedUser)
    }

    private fun mapUserToResponse(user: User): UserResponse {
        return UserResponse(
            id = user.id,
            email = user.email,
            status = user.status,
            provider = user.provider,
            roles = user.roles.map { it.name }.toSet()
        )
    }
}
