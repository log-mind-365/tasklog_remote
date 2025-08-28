package com.logmind.tasklog_server.service

import com.logmind.tasklog_server.entity.RefreshToken
import com.logmind.tasklog_server.exception.AuthServiceException
import com.logmind.tasklog_server.exception.InvalidRefreshTokenException
import com.logmind.tasklog_server.exception.RefreshTokenExpiredException
import com.logmind.tasklog_server.exception.RefreshTokenRevokedException
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
        return execute("create refresh token") {
            val token = UUID.randomUUID().toString()
            val expiresAt = LocalDateTime.now().plusSeconds(jwtProperties.refreshTokenExpiration / 1000)
            
            val refreshToken = RefreshToken(
                token = token,
                userEmail = userEmail,
                expiresAt = expiresAt
            )
            
            refreshTokenRepository.save(refreshToken)
        }
    }

    fun validateRefreshToken(token: String): RefreshToken {
        val refreshToken = refreshTokenRepository.findByToken(token)
            ?: throw InvalidRefreshTokenException("Invalid refresh token")

        if (refreshToken.revoked) {
            throw RefreshTokenRevokedException("Refresh token has been revoked")
        }

        if (refreshToken.expiresAt.isBefore(LocalDateTime.now())) {
            throw RefreshTokenExpiredException("Refresh token has expired")
        }

        return refreshToken
    }

    fun revokeToken(token: String) {
        execute("revoke refresh token") {
            refreshTokenRepository.revokeToken(token)
            logger.info("Refresh token revoked")
        }
    }

    fun revokeAllUserTokens(userEmail: String) {
        execute("revoke all user tokens") {
            refreshTokenRepository.revokeAllUserTokens(userEmail)
            logger.info("All refresh tokens revoked for user: $userEmail")
        }
    }

    fun cleanupExpiredTokens() {
        execute("cleanup expired tokens") {
            refreshTokenRepository.deleteExpiredTokens(LocalDateTime.now())
            logger.info("Expired refresh tokens cleaned up")
        }
    }

    private inline fun <T> execute(operation: String, block: () -> T): T {
        return runCatching {
            block()
        }.getOrElse {
            logger.error("Failed execute $operation - ${it.message}")
            when (it) {
                is InvalidRefreshTokenException,
                is RefreshTokenExpiredException,
                is RefreshTokenRevokedException -> throw it
                else -> throw AuthServiceException("Failed execute $operation", it)
            }
        }
    }
}