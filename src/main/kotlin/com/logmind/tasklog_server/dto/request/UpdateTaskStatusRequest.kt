package com.logmind.tasklog_server.dto.request

data class UpdateTaskStatusRequest(
    val id: Long,
    val isCompleted: Boolean
)