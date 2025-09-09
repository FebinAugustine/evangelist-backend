package com.febin.evangelist.domain.repository

import com.febin.evangelist.domain.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): Optional<User>
    fun findByVerificationCode(code: String): Optional<User>
    fun existsByEmail(email: String): Boolean
}
