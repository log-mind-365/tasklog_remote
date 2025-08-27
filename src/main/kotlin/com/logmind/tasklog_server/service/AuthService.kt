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
        return runCatching {
            userRepository.findByEmail(addUserRequest.email)?.let {
                throw AlreadyEmailRegisteredException()
            }
            val user = User(
                email = addUserRequest.email,
                password = passwordEncoder.encode(addUserRequest.password),
                username = addUserRequest.email
            )
            val savedUser = userRepository.save(user)

            tokenService.createToken(savedUser)
        }.getOrElse {
            when (it) {
                is AlreadyEmailRegisteredException -> throw it
                else -> throw AuthServiceException("Register failed", it)
            }
        }
    }

    fun login(loginRequest: LoginRequest): AuthResponse {
        return runCatching {
            val authToken = UsernamePasswordAuthenticationToken(
                loginRequest.email, loginRequest.password
            )
            val authentication = authenticationManager.authenticate(
                authToken
            )
            val user = authentication.principal as User

            tokenService.createToken(user)
        }.getOrElse {
            when (it) {
                is BadCredentialsException -> throw InvalidCredentialsException()
                else -> throw AuthServiceException("Login failed", it)
            }
        }

    }

    fun logout(request: HttpServletRequest, response: HttpServletResponse) {
        return runCatching {
            val authentication = SecurityContextHolder.getContext().authentication
            SecurityContextLogoutHandler().logout(request, response, authentication)
        }.getOrElse {
            throw AuthServiceException("Logout failed", it)
        }
    }
}