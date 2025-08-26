package com.logmind.tasklog_server.exception

import java.time.LocalDateTime

interface ErrorCode {
    val code: String
    val message: String
    val httpStatus: Int
}

open class BaseException(
    val errorCode: ErrorCode,
    message: String? = null,
    cause: Throwable? = null
) : RuntimeException(message ?: errorCode.message, cause) {
    val httpStatus = errorCode.httpStatus
    val code = errorCode.code
}

data class ErrorResponse(
    val success: Boolean = false,
    val errorCode: String,
    val message: String,
    val timestamp: LocalDateTime = LocalDateTime.now()
)