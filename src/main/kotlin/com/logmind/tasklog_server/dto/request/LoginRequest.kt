package com.logmind.tasklog_server.dto.request

data class LoginRequest(
    val email: String,
    val password: String,
)