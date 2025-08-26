package com.logmind.tasklog_server.exception

enum class AuthErrorCode(
    override val code: String,
    override val message: String,
    override val httpStatus: Int = 401
) : ErrorCode {
    INVALID_CREDENTIALS("AUTH_001", "Invalid credentials"),
    INVALID_TOKEN("AUTH_002", "Invalid token"),
    EXPIRED_TOKEN("AUTH_003", "Expired token"),
    TOKEN_REQUIRED("AUTH_004", "Token required"),
    ALREADY_EMAIL_REGISTERED("AUTH_005", "Already registered email"),
    SERVICE_ERROR("AUTH_999", "An error occurred in the authentication service", 500)
}

open class AuthException(
    errorCode: AuthErrorCode,
    message: String? = null,
    cause: Throwable? = null
) : BaseException(errorCode, message, cause)

class InvalidCredentialsException(
    message: String? = null,
    cause: Throwable? = null
) : AuthException(AuthErrorCode.INVALID_CREDENTIALS, message, cause)

class InvalidTokenException(
    message: String? = null,
    cause: Throwable? = null
) : AuthException(AuthErrorCode.INVALID_TOKEN, message, cause)

class ExpiredTokenException(
    message: String? = null,
    cause: Throwable? = null
) : AuthException(AuthErrorCode.EXPIRED_TOKEN, message, cause)

class TokenRequiredException(
    message: String? = null,
    cause: Throwable? = null
) : AuthException(AuthErrorCode.TOKEN_REQUIRED, message, cause)

class AlreadyEmailRegisteredException(
    message: String? = null,
    cause: Throwable? = null
) : AuthException(
    AuthErrorCode.ALREADY_EMAIL_REGISTERED, message, cause
)

class AuthServiceException(
    message: String? = null,
    cause: Throwable? = null
) : AuthException(AuthErrorCode.SERVICE_ERROR, message, cause)