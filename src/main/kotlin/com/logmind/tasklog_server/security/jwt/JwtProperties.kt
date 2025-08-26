package com.logmind.tasklog_server.security.jwt

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("jwt")
data class JwtProperties(
    var secret: String = "",
    var issuer: String = "",
    var accessTokenExpiration: Long = 86400000L, // 24시간
    var refreshTokenExpiration: Long = 604800000L // 7일
)