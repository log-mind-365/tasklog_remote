package com.logmind.tasklog_server.dto.request

data class AddTaskRequest(
    val title: String,
    val description: String,
)