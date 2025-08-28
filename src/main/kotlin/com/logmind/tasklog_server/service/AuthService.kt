package com.logmind.tasklog_server.service

import com.logmind.tasklog_server.dto.request.LoginRequest
import com.logmind.tasklog_server.dto.request.RegisterRequest
import com.logmind.tasklog_server.dto.response.AuthResponse
import com.logmind.tasklog_server.entity.User
import com.logmind.tasklog_server.exception.AlreadyEmailRegisteredException
import com.logmind.tasklog_server.exception.AuthServiceException
import com.logmind.tasklog_server.exception.InvalidCredentialsException
import com.logmind.tasklog_server.repository.UserRepository
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val tokenService: TokenService,
    private val authenticationManager: AuthenticationManager
) {
    @Transactional
    fun register(addUserRequest: RegisterRequest): AuthResponse {
        return execute("register") {
            if (userRepository.existsByEmail(addUserRequest.email)) {
                throw AlreadyEmailRegisteredException()
            }

            val user = User(
                email = addUserRequest.email,
                password = passwordEncoder.encode(addUserRequest.password),
                username = addUserRequest.email
            )
            val savedUser = userRepository.save(user)
            tokenService.createToken(savedUser)
        }
    }

    @Transactional(readOnly = true)
    fun login(loginRequest: LoginRequest): AuthResponse {
        val (email, password) = loginRequest
        return execute("login") {
            val authToken = UsernamePasswordAuthenticationToken(email, password)
            val authentication = authenticationManager.authenticate(
                authToken
            )
            val user = authentication.principal as User
            tokenService.createToken(user)
        }

    }

    fun logout(request: HttpServletRequest, response: HttpServletResponse) {
        return execute("logout") {
            val authentication = SecurityContextHolder.getContext().authentication
            if (authentication != null && authentication.principal is User) {
                val user = authentication.principal as User
                tokenService.revokeAllUserTokens(user.email)
            }
            SecurityContextLogoutHandler().logout(request, response, authentication)
        }
    }

    private inline fun <T> execute(operation: String, block: () -> T): T {
        return runCatching {
            block()
        }.getOrElse {
            when (it) {
                is AlreadyEmailRegisteredException -> throw it
                is BadCredentialsException -> throw InvalidCredentialsException()
                else -> throw AuthServiceException("Failed execute $operation", it)
            }
        }
    }
}