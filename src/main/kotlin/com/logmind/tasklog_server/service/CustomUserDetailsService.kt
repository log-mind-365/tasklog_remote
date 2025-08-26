package com.logmind.tasklog_server.service

import com.logmind.tasklog_server.entity.User
import com.logmind.tasklog_server.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service


@Service
class CustomUserDetailsService(private val userRepository: UserRepository) : UserDetailsService {
    override fun loadUserByUsername(email: String): User {
        return userRepository.findByEmail(email)
            ?: throw UsernameNotFoundException("User not found")
    }
}