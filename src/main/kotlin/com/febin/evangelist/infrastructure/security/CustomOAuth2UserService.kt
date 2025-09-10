package com.febin.evangelist.infrastructure.security

import com.febin.evangelist.domain.model.AccountStatus
import com.febin.evangelist.domain.model.User
import com.febin.evangelist.domain.model.UserProvider
import com.febin.evangelist.domain.repository.UserRepository
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

@Service
class CustomOAuth2UserService(
    private val userRepository: UserRepository
) : DefaultOAuth2UserService() {

    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val oAuth2User = super.loadUser(userRequest)
        return processOAuth2User(userRequest, oAuth2User)
    }

    private fun processOAuth2User(userRequest: OAuth2UserRequest, oAuth2User: OAuth2User): OAuth2User {
        val attributes = oAuth2User.attributes
        val email = attributes["email"] as String
        val provider = UserProvider.valueOf(userRequest.clientRegistration.registrationId.uppercase())

        val userOptional = userRepository.findBy_email(email)
        val user: User

        if (userOptional.isPresent) {
            user = userOptional.get()
            // Update provider and providerId if user logs in with a new OAuth2 provider
            if (user.provider != provider) {
                user.provider = provider
                user.providerId = oAuth2User.name
            }
        } else {
            user = registerNewOAuth2User(provider, oAuth2User)
        }

        user.setAttributes(attributes)
        return user
    }

    private fun registerNewOAuth2User(provider: UserProvider, oAuth2User: OAuth2User): User {
        val attributes = oAuth2User.attributes
        val email = attributes["email"] as String
        val providerId = oAuth2User.name

        val newUser = User(
            _email = email,
            provider = provider,
            providerId = providerId,
            status = AccountStatus.ACTIVE // OAuth2 users are active by default
        )
        return userRepository.save(newUser)
    }
}
