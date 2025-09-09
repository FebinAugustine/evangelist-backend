package com.febin.evangelist.application.service

import com.febin.evangelist.application.dto.UpdateUserRolesRequest
import com.febin.evangelist.application.dto.UserResponse

/**
 * Defines the contract for administrative operations.
 */
interface AdminService {
    /**
     * Retrieves a list of all users.
     *
     * @return A list of DTOs containing user information.
     */
    fun getAllUsers(): List<UserResponse>

    /**
     * Retrieves the details of a specific user by their ID.
     *
     * @param id The ID of the user to retrieve.
     * @return A DTO containing the user's profile information.
     */
    fun getUserById(id: Long): UserResponse

    /**
     * Updates the roles for a specific user.
     *
     * @param id The ID of the user to update.
     * @param request The request containing the new set of roles.
     * @return A DTO containing the updated user's profile information.
     */
    fun updateUserRoles(id: Long, request: UpdateUserRolesRequest): UserResponse
}
