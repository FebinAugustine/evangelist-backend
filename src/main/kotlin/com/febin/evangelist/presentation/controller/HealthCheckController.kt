package com.febin.evangelist.presentation.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/**
 * A simple controller for checking the health of the application.
 */
@RestController
class HealthCheckController {

    /**
     * Returns a simple message to indicate that the application is running.
     */
    @GetMapping("/")
    fun healthCheck(): ResponseEntity<String> {
        return ResponseEntity.ok("Application is running.")
    }
}
