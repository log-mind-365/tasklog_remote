package com.logmind.tasklog_server.dto.response

import com.logmind.tasklog_server.entity.LoginUserInfo

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer",
    val expiresIn: Long,
    val data: LoginUserInfo,
)