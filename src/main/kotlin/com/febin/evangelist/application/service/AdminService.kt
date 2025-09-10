package com.febin.evangelist.application.service

import com.febin.evangelist.application.dto.MessageResponse
import com.febin.evangelist.application.dto.UpdateUserRolesRequest
import com.febin.evangelist.application.dto.UserResponse
import com.febin.evangelist.domain.model.Role

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

    /**
     * Retrieves a list of all available roles.
     *
     * @return A list of all Role entities.
     */
    fun getAllRoles(): List<Role>

    /**
     * Disables a user's account.
     *
     * @param id The ID of the user to disable.
     * @return A DTO containing the updated user's profile information.
     */
    fun disableUser(id: Long): UserResponse

    /**
     * Enables a user's account.
     *
     * @param id The ID of the user to enable.
     * @return A DTO containing the updated user's profile information.
     */
    fun enableUser(id: Long): UserResponse

    /**
     * Deletes a user's account permanently.
     *
     * @param id The ID of the user to delete.
     * @return A message response indicating success.
     */
    fun deleteUser(id: Long): MessageResponse
}
