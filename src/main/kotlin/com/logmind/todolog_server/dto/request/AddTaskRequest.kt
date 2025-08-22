package com.logmind.todolog_server.dto.request

data class AddTaskRequest(
    val title: String,
    val description: String,
)