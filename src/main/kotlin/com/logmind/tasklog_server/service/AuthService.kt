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
import org.slf4j.LoggerFactory
import org.springframework.dao.DataAccessException
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val tokenService: TokenService,
    private val authenticationManager: AuthenticationManager
) {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    fun register(addUserRequest: RegisterRequest): Result<AuthResponse> {
        return try {
            if (userRepository.findByEmail(addUserRequest.email) != null) {
                return Result.failure(AlreadyEmailRegisteredException())
            }
            val user = User(
                email = addUserRequest.email,
                password = passwordEncoder.encode(addUserRequest.password),
                username = addUserRequest.username,
            )
            val savedUser = userRepository.save(user)
            val authResponse = tokenService.createToken(savedUser)
            Result.success(authResponse)
        } catch (e: DataAccessException) {
            Result.failure(AuthServiceException("Database error while register", e))
        } catch (e: Exception) {
            Result.failure(AuthServiceException("Failed to register", e))
        }
    }

    fun login(loginRequest: LoginRequest): Result<AuthResponse> {
        return try {
            val user = userRepository.findByEmail(loginRequest.email)
                ?: return Result.failure(InvalidCredentialsException())

            if (!passwordEncoder.matches(loginRequest.password, user.password)) {
                return Result.failure(Exception(InvalidCredentialsException()))
            }
            val authResponse = tokenService.createToken(user)
            Result.success(authResponse)
        } catch (e: DataAccessException) {
            Result.failure(AuthServiceException("Database error while login", e))
        } catch (e: Exception) {
            Result.failure(AuthServiceException("Failed to login", e))
        }
    }

    fun logout(request: HttpServletRequest, response: HttpServletResponse): Result<Unit> {
        return try {
            val authentication = SecurityContextHolder.getContext().authentication
            if (authentication != null) {
                SecurityContextLogoutHandler().logout(request, response, authentication)
                logger.info("User logged out successfully")
            } else {
                logger.warn("No authentication found during logout")
            }
            Result.success(Unit)
        } catch (e: DataAccessException) {
            Result.failure(Exception("Database error while logout", e))
        } catch (e: Exception) {
            Result.failure(Exception("Logout failed", e))
        }
    }

}