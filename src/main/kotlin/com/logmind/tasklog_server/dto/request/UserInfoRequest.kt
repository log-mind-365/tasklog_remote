package com.logmind.tasklog_server.dto.request

data class UserInfoRequest(
    val id: Long,
    val accessToken: String,
    val refreshToken: String,
)