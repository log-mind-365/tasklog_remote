package com.logmind.tasklog_server.service

import com.logmind.tasklog_server.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val tokenService: TokenService
) {
    fun getUserById(id: Long) {
        val user = userRepository.findById(id).orElse(null)
    }
}