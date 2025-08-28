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
    private val jwtProperties: JwtProperties,
    private val refreshTokenService: RefreshTokenService,
    private val userService: UserService
) {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    fun createToken(user: User): AuthResponse {
        return execute("create token") {
            val authentication = createAuthentication(user)
            val accessToken = jwtTokenProvider.generateAccessToken(authentication)
            val refreshTokenEntity = refreshTokenService.createRefreshToken(user.email)
            logger.info("Tokens issued for user: ${user.email}")
            AuthResponse(
                accessToken = accessToken,
                refreshToken = refreshTokenEntity.token,
                tokenType = "Bearer",
                expiresIn = jwtProperties.accessTokenExpiration,
                data = user.toLoginUserInfo(),
            )
        }
    }

    private fun createAuthentication(user: User): Authentication {
        return execute("create authentication") {
            UsernamePasswordAuthenticationToken(
                user,
                null,
                user.authorities
            )
        }
    }

    fun refreshAccessToken(refreshToken: String): AuthResponse {
        return execute("refresh access token") {
            val refreshTokenEntity = refreshTokenService.validateRefreshToken(refreshToken)
            val user = userService.getUserByEmail(refreshTokenEntity.userEmail)
            val authentication = createAuthentication(user)
            val newAccessToken = jwtTokenProvider.generateAccessToken(authentication)

            logger.info("Access token refreshed for user: ${user.email}")
            AuthResponse(
                accessToken = newAccessToken,
                refreshToken = refreshToken,
                tokenType = "Bearer",
                expiresIn = jwtProperties.accessTokenExpiration,
                data = user.toLoginUserInfo(),
            )
        }
    }

    fun revokeRefreshToken(refreshToken: String) {
        execute("revoke refresh token") {
            refreshTokenService.revokeToken(refreshToken)
            logger.info("Refresh token revoked")
        }
    }

    fun revokeAllUserTokens(userEmail: String) {
        execute("revoke all user tokens") {
            refreshTokenService.revokeAllUserTokens(userEmail)
            logger.info("All refresh tokens revoked for user: $userEmail")
        }
    }

    private inline fun <T> execute(operation: String, block: () -> T): T {
        return runCatching {
            block()
        }.getOrElse {
            logger.error("Failed execute $operation - ${it.message}")
            throw Exception("Failed execute $operation", it)
        }
    }
}