package com.logmind.tasklog_server.config.jwt

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("jwt")
data class JwtProperties(
    var secret: String = "",
    var issuer: String = ""
)