package com.logmind.tasklog_server.exception

enum class UserErrorCode(
    override val code: String,
    override val message: String,
    override val httpStatus: Int = 401
) : ErrorCode {
    USER_NOT_FOUND("USER_001", "User not found"),
    SERVICE_ERROR("USER_999", "An error occurred in the user service", 500)
}

open class UserException(
    errorCode: UserErrorCode,
    message: String? = null,
    cause: Throwable? = null
) : BaseException(errorCode, message, cause)

class UserNotFoundException(
    message: String? = null,
    cause: Throwable? = null
) : UserException(
    UserErrorCode.USER_NOT_FOUND, message, cause
)

class UserServiceException(
    message: String? = null,
    cause: Throwable? = null
) : UserException(UserErrorCode.SERVICE_ERROR, message, cause)