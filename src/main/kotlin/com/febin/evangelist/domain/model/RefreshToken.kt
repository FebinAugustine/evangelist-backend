package com.febin.evangelist.domain.model

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "refresh_tokens")
data class RefreshToken(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    val user: User,

    @Column(nullable = false, unique = true)
    val token: String,

    @Column(nullable = false)
    val expiryDate: Instant
)
