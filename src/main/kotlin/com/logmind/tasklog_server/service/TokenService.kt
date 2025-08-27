package com.logmind.tasklog_server.service

import com.logmind.tasklog_server.dto.response.AuthResponse
import com.logmind.tasklog_server.entity.User
import com.logmind.tasklog_server.entity.toLoginUserInfo
import com.logmind.tasklog_server.security.jwt.JwtProperties
import com.logmind.tasklog_server.security.jwt.JwtTokenProvider
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service

@Service
class TokenService(
    private val jwtTokenProvider: JwtTokenProvider,
    private val jwtProperties: JwtProperties
) {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    fun createToken(user: User): AuthResponse {
        val authentication = createAuthentication(user)

        val accessToken = jwtTokenProvider.generateAccessToken(authentication)
        val refreshToken = jwtTokenProvider.generateRefreshToken(authentication)

        logger.info("Tokens issued for user: ${user.email}")

        return AuthResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            tokenType = "Bearer",
            expiresIn = jwtProperties.accessTokenExpiration,
            data = user.toLoginUserInfo(),
        )
    }

    fun refreshToken(refreshToken: String): Result<AuthResponse> {
        return try {
            if (!jwtTokenProvider.validateToken(refreshToken)) {
                return Result.failure(Exception("Invalid refresh token"))
            }

            val username = jwtTokenProvider.getUsernameFromToken(refreshToken)
            // 실제 구현에서는 UserService를 통해 사용자를 조회해야 합니다
            // 여기서는 간단히 처리
            logger.info("Token refreshed for user: $username")

            // TODO: UserService를 주입받아 실제 User 객체를 조회
            Result.failure(Exception("Token refresh not fully implemented"))
        } catch (e: Exception) {
            logger.error("Token refresh failed", e)
            Result.failure(Exception("Token refresh failed"))
        }
    }

    private fun createAuthentication(user: User): Authentication {
        return UsernamePasswordAuthenticationToken(
            user,
            null,
            user.authorities
        )
    }
}