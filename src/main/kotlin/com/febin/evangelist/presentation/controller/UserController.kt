package com.febin.evangelist.presentation.controller

import com.febin.evangelist.application.dto.ChangePasswordRequest
import com.febin.evangelist.application.dto.MessageResponse
import com.febin.evangelist.application.dto.UserResponse
import com.febin.evangelist.application.service.UserService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {

    @GetMapping("/me")
    fun getCurrentUser(): ResponseEntity<UserResponse> {
        val userResponse = userService.getCurrentUser()
        return ResponseEntity.ok(userResponse)
    }

    @DeleteMapping("/me")
    fun deleteCurrentUser(): ResponseEntity<MessageResponse> {
        val messageResponse = userService.deleteCurrentUser()
        return ResponseEntity.ok(messageResponse)
    }

    @PostMapping("/me/password")
    fun changePassword(@Valid @RequestBody request: ChangePasswordRequest): ResponseEntity<MessageResponse> {
        val messageResponse = userService.changePassword(request)
        return ResponseEntity.ok(messageResponse)
    }
}
