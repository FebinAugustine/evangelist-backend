package com.febin.evangelist.infrastructure.security

import com.febin.evangelist.application.service.RefreshTokenService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseCookie
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class OAuth2AuthenticationSuccessHandler(
    private val jwtTokenProvider: JwtTokenProvider,
    private val refreshTokenService: RefreshTokenService,
    @Value("\${app.oauth2.redirect-uri:http://localhost:3000/}") private val redirectUri: String
) : SimpleUrlAuthenticationSuccessHandler() {

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val accessToken = jwtTokenProvider.generateToken(authentication)
        val refreshToken = refreshTokenService.createRefreshToken(authentication.principal as com.febin.evangelist.domain.model.User)

        val accessTokenCookie = ResponseCookie.from("access_token", accessToken)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(60 * 60) // 1 hour
            .build()

        val refreshTokenCookie = ResponseCookie.from("refresh_token", refreshToken.token)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(7 * 24 * 60 * 60) // 7 days
            .build()

        response.addHeader("Set-Cookie", accessTokenCookie.toString())
        response.addHeader("Set-Cookie", refreshTokenCookie.toString())

        clearAuthenticationAttributes(request)
        redirectStrategy.sendRedirect(request, response, redirectUri)
    }
}
