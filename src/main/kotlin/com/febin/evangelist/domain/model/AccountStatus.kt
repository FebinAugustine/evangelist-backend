package com.febin.evangelist.domain.model

/**
 * Represents the status of a user's account.
 */
enum class AccountStatus {
    /**
     * The user has registered, but has not yet verified their email address.
     */
    UNVERIFIED,

    /**
     * The user has verified their email and their account is active.
     */
    ACTIVE,

    /**
     * The user's account has been disabled by an administrator.
     */
    DISABLED
}
