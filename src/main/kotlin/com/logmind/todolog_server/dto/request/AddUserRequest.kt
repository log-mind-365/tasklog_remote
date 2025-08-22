package com.logmind.todolog_server.dto.request

data class AddUserRequest(
    val email: String,
    val name: String,
    val password: String,
)