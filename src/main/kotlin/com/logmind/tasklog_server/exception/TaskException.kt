package com.logmind.tasklog_server.exception

enum class TaskErrorCode(
    override val code: String,
    override val message: String,
    override val httpStatus: Int = 500
) : ErrorCode {
    NOT_FOUND("TASK_001", "Task not found", 404),
    TASK_ACCESS_DENIED("TASK_002", "Task access denied", 403),
    SERVICE_ERROR("TASK_999", "An error occurred in the task service", 500)
}

open class TaskException(
    errorCode: TaskErrorCode,
    message: String? = null,
    cause: Throwable? = null
) : BaseException(errorCode, message, cause)

class TaskNotFoundException(
    message: String? = null,
    cause: Throwable? = null
) : TaskException(TaskErrorCode.NOT_FOUND, message, cause)

class TaskAccessDeniedException(
    message: String? = null,
    cause: Throwable? = null
) : TaskException(TaskErrorCode.TASK_ACCESS_DENIED, message, cause)

class TaskServiceException(
    message: String? = null,
    cause: Throwable? = null
) : TaskException(TaskErrorCode.SERVICE_ERROR, message, cause)

