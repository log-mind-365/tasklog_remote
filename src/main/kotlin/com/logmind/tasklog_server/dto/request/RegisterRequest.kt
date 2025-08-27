package com.logmind.tasklog_server.dto.request

data class RegisterRequest(
    val email: String,
    val password: String,
)