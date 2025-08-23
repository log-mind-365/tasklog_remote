package com.logmind.tasklog_server.dto.request

data class UpdateTaskRequest(
    val id: Long,
    val title: String,
    val description: String,
    val isCompleted: Boolean
)
