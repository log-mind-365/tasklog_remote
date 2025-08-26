package com.logmind.tasklog_server.dto.request

data class UpdateUserInfoRequest(
    val id: Long,
    val username: String,
)