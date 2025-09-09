package com.febin.evangelist.presentation.controller

import com.febin.evangelist.application.dto.MessageResponse
import com.febin.evangelist.application.dto.UserResponse
import com.febin.evangelist.application.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
}
