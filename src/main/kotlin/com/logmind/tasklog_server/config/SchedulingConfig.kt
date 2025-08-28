package com.logmind.tasklog_server.config

import com.logmind.tasklog_server.service.RefreshTokenService
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled

@Configuration
@EnableScheduling
class SchedulingConfig(
    private val refreshTokenService: RefreshTokenService
) {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Scheduled(fixedRate = 3600000) // 1시간마다 실행
    fun cleanupExpiredTokens() {
        logger.info("Starting cleanup of expired refresh tokens")
        refreshTokenService.cleanupExpiredTokens()
        logger.info("Completed cleanup of expired refresh tokens")
    }
}