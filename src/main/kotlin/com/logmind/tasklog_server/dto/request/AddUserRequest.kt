package com.logmind.tasklog_server.dto.request

data class AddUserRequest(
    val email: String,
    val name: String,
    val password: String,
)