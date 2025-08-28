package com.logmind.tasklog_server.service

import com.logmind.tasklog_server.entity.RefreshToken
import com.logmind.tasklog_server.exception.TokenException
import com.logmind.tasklog_server.repository.RefreshTokenRepository
import com.logmind.tasklog_server.security.jwt.JwtProperties
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

@Service
@Transactional
class RefreshTokenService(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val jwtProperties: JwtProperties
) {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    fun createRefreshToken(userEmail: String): RefreshToken {
        val token = UUID.randomUUID().toString()
        val expiresAt = LocalDateTime.now().plusSeconds(jwtProperties.refreshTokenExpiration / 1000)
        
        val refreshToken = RefreshToken(
            token = token,
            userEmail = userEmail,
            expiresAt = expiresAt
        )
        
        return refreshTokenRepository.save(refreshToken)
    }

    fun validateRefreshToken(token: String): RefreshToken {
        val refreshToken = refreshTokenRepository.findByToken(token)
            ?: throw TokenException("Invalid refresh token")

        if (refreshToken.revoked) {
            throw TokenException("Refresh token has been revoked")
        }

        if (refreshToken.expiresAt.isBefore(LocalDateTime.now())) {
            throw TokenException("Refresh token has expired")
        }

        return refreshToken
    }

    fun revokeToken(token: String) {
        refreshTokenRepository.revokeToken(token)
        logger.info("Refresh token revoked")
    }

    fun revokeAllUserTokens(userEmail: String) {
        refreshTokenRepository.revokeAllUserTokens(userEmail)
        logger.info("All refresh tokens revoked for user: $userEmail")
    }

    fun cleanupExpiredTokens() {
        refreshTokenRepository.deleteExpiredTokens(LocalDateTime.now())
        logger.info("Expired refresh tokens cleaned up")
    }
}