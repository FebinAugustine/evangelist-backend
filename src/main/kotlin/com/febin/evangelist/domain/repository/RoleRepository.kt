package com.febin.evangelist.domain.repository

import com.febin.evangelist.domain.model.Role
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RoleRepository : JpaRepository<Role, Long> {
    fun findByNameIn(names: List<String>): List<Role>
}
