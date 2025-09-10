package com.febin.evangelist.application.service

import com.febin.evangelist.application.dto.ChangePasswordRequest
import com.febin.evangelist.application.dto.MessageResponse
import com.febin.evangelist.application.dto.UserResponse

/**
 * Defines the contract for user-related operations.
 */
interface UserService {
    /**
     * Retrieves the profile of the currently authenticated user.
     *
     * @return A DTO containing the user's profile information.
     */
    fun getCurrentUser(): UserResponse

    /**
     * Deletes the account of the currently authenticated user.
     *
     * @return A message response indicating success.
     */
    fun deleteCurrentUser(): MessageResponse

    /**
     * Changes the password for the currently authenticated user.
     *
     * @param request The request containing the current and new passwords.
     * @return A message response indicating success.
     */
    fun changePassword(request: ChangePasswordRequest): MessageResponse
}
