package com.febin.evangelist.presentation.controller

import com.febin.evangelist.application.dto.UpdateUserRolesRequest
import com.febin.evangelist.application.dto.UserResponse
import com.febin.evangelist.application.service.AdminService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/admin")
class AdminController(
    private val adminService: AdminService
) {

    @GetMapping("/users")
    fun getAllUsers(): ResponseEntity<List<UserResponse>> {
        val users = adminService.getAllUsers()
        return ResponseEntity.ok(users)
    }

    @GetMapping("/users/{id}")
    fun getUserById(@PathVariable id: Long): ResponseEntity<UserResponse> {
        val user = adminService.getUserById(id)
        return ResponseEntity.ok(user)
    }

    @PutMapping("/users/{id}/roles")
    fun updateUserRoles(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateUserRolesRequest
    ): ResponseEntity<UserResponse> {
        val updatedUser = adminService.updateUserRoles(id, request)
        return ResponseEntity.ok(updatedUser)
    }
}
