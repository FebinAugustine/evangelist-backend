package com.febin.evangelist.presentation.exception

import com.febin.evangelist.application.dto.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(ex: RuntimeException, request: WebRequest): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            statusCode = HttpStatus.BAD_REQUEST.value(),
            message = ex.message ?: "An unexpected error occurred",
            description = request.getDescription(false)
        )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException, request: WebRequest): ResponseEntity<ErrorResponse> {
        val errors = ex.bindingResult.fieldErrors
            .map { it.defaultMessage }
            .joinToString(", ")

        val errorResponse = ErrorResponse(
            statusCode = HttpStatus.BAD_REQUEST.value(),
            message = "Validation failed: $errors",
            description = request.getDescription(false)
        )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(Exception::class)
    fun handleGlobalException(ex: Exception, request: WebRequest): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            message = "An internal server error has occurred.",
            description = request.getDescription(false)
        )
        return ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
