package com.febin.evangelist.infrastructure.security

import com.febin.evangelist.domain.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserDetailsServiceImpl(
    private val userRepository: UserRepository
) : UserDetailsService {

    @Transactional
    override fun loadUserByUsername(username: String): UserDetails {
        return userRepository.findByEmail(username)
            .orElseThrow { UsernameNotFoundException("User Not Found with email: $username") }
    }
}
