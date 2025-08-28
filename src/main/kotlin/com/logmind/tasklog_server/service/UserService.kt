package com.logmind.tasklog_server.service

import com.logmind.tasklog_server.entity.User
import com.logmind.tasklog_server.exception.UserNotFoundException
import com.logmind.tasklog_server.exception.UserServiceException
import com.logmind.tasklog_server.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
) {
    fun getUserById(id: Long): User {
        return execute("get user by id") {
            userRepository.findById(id).orElseThrow { UserNotFoundException() }
        }
    }

    fun getUserByEmail(email: String): User {
        return execute("get user by email") {
            userRepository.findByEmail(email) ?: throw UserNotFoundException()
        }
    }

    private inline fun <T> execute(operation: String, block: () -> T): T {
        return runCatching {
            block()
        }.getOrElse {
            when (it) {
                is IllegalArgumentException -> throw it
                is UserNotFoundException -> throw it
                else -> throw UserServiceException("Failed execute $operation - ${it.message}", it)
            }
        }
    }
}