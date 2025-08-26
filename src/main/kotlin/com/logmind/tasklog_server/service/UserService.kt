package com.logmind.tasklog_server.service

import com.logmind.tasklog_server.dto.request.UserInfoRequest
import com.logmind.tasklog_server.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val tokenService: TokenService
) {
    fun getUserInfo(userInfoRequest: UserInfoRequest) {
        val user = userRepository.findById(userInfoRequest.id).orElse(null)

    }

}