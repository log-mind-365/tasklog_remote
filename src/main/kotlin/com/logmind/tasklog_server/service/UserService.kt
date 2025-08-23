package com.logmind.tasklog_server.service

import com.logmind.tasklog_server.entity.User
import com.logmind.tasklog_server.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(val userRepository: UserRepository) {
    fun findUserById(id:Long): Result<User> {
        val user = userRepository.findById(id).orElse(null)
        if (user != null) {
            return Result.success(user)
        }
        return Result.failure(Exception("User not found"))
    }
}