package com.febin.evangelist.application.service

import com.febin.evangelist.domain.model.User

/**
 * Defines the contract for a service that sends emails.
 */
interface EmailService {
    /**
     * Sends a verification email to the user.
     *
     * @param user The user to send the verification email to.
     */
    fun sendVerificationEmail(user: User)

    /**
     * Sends a password reset email to the user.
     *
     * @param user The user to send the password reset email to.
     */
    fun sendPasswordResetEmail(user: User)
}
