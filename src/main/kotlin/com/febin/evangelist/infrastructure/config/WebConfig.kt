package com.febin.evangelist.infrastructure.config

import com.febin.evangelist.infrastructure.ratelimit.RateLimitingInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(private val rateLimitingInterceptor: RateLimitingInterceptor) : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        // Apply the rate-limiting interceptor to all API endpoints
        registry.addInterceptor(rateLimitingInterceptor)
            .addPathPatterns("/api/**")
    }
}
