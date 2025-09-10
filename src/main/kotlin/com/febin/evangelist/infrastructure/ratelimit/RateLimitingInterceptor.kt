package com.febin.evangelist.infrastructure.ratelimit

import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

@Component
class RateLimitingInterceptor : HandlerInterceptor {

    private val buckets = ConcurrentHashMap<String, Bucket>()

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val ip = getClientIp(request)
        val bucket = buckets.computeIfAbsent(ip) { createNewBucket() }

        if (bucket.tryConsume(1)) {
            return true // Request is allowed
        } else {
            response.status = HttpStatus.TOO_MANY_REQUESTS.value()
            response.writer.write("Too many requests")
            return false // Request is blocked
        }
    }

    private fun createNewBucket(): Bucket {
        // Allow 100 requests per minute
        val limit = Bandwidth.simple(100, Duration.ofMinutes(1))
        return Bucket.builder().addLimit(limit).build()
    }

    private fun getClientIp(request: HttpServletRequest): String {
        val xForwardedForHeader = request.getHeader("X-Forwarded-For")
        return if (xForwardedForHeader.isNullOrBlank()) {
            request.remoteAddr
        } else {
            xForwardedForHeader.split(",").first().trim()
        }
    }
}
