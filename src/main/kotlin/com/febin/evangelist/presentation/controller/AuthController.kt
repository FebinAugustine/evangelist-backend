package com.febin.evangelist.presentation.controller

import com.febin.evangelist.application.dto.LoginRequest
import com.febin.evangelist.application.dto.MessageResponse
import com.febin.evangelist.application.dto.SignupRequest
import com.febin.evangelist.application.service.AuthService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/signup")
    fun registerUser(@Valid @RequestBody signupRequest: SignupRequest): ResponseEntity<MessageResponse> {
        val response = authService.registerUser(signupRequest)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/verify")
    fun verifyUser(@RequestParam("code") code: String): ResponseEntity<MessageResponse> {
        val response = authService.verifyUser(code)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/login")
    fun authenticateUser(@Valid @RequestBody loginRequest: LoginRequest): ResponseEntity<MessageResponse> {
        return authService.authenticateUser(loginRequest)
    }
}
