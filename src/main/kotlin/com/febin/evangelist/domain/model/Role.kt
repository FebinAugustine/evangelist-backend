package com.febin.evangelist.domain.model

import jakarta.persistence.*

/**
 * Represents a role that can be assigned to a user (e.g., ROLE_USER, ROLE_ADMIN).
 */
@Entity
@Table(name = "roles")
data class Role(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(length = 20, unique = true, nullable = false)
    val name: String
)
