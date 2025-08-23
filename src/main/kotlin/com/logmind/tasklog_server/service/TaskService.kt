package com.logmind.tasklog_server.service

import com.logmind.tasklog_server.dto.request.AddTaskRequest
import com.logmind.tasklog_server.dto.request.UpdateTaskRequest
import com.logmind.tasklog_server.dto.request.UpdateTaskStatusRequest
import com.logmind.tasklog_server.entity.Task
import com.logmind.tasklog_server.repository.TaskRepository
import org.slf4j.LoggerFactory
import org.springframework.dao.DataAccessException
import org.springframework.stereotype.Service

class TaskNotFoundException(id: Long) : Exception("Task with id $id not found")
class TaskServiceException(message: String, cause: Throwable? = null) : Exception(message, cause)

@Service
class TaskService(
    val taskRepository: TaskRepository
) {
    private val logger = LoggerFactory.getLogger(TaskService::class.java)
    fun save(req: AddTaskRequest): Result<Task> {
        val newTask = Task(title = req.title, description = req.description)
        return try {
            val task = taskRepository.save(newTask)
            Result.success(task)
        } catch (e: DataAccessException) {
            logger.error("Database error while saving task", e)
            Result.failure(TaskServiceException("Failed to save task", e))
        } catch (e: Exception) {
            logger.error("Unexpected error while saving task", e)
            Result.failure(e)
        }
    }

    fun delete(id: Long): Result<Boolean> {
        return try {
            taskRepository.deleteTaskById(id)
            Result.success(true)
        } catch (e: DataAccessException) {
            logger.error("Database error while deleting task with id: $id", e)
            Result.failure(TaskServiceException("Failed to delete task", e))
        } catch (e: Exception) {
            logger.error("Unexpected error while deleting task with id: $id", e)
            Result.failure(e)
        }
    }

    fun findAll(): Result<List<Task>> {
        return try {
            val tasks = taskRepository.findAll()
            Result.success(tasks)
        } catch (e: DataAccessException) {
            logger.error("Database error while finding all tasks", e)
            Result.failure(TaskServiceException("Failed to retrieve tasks", e))
        } catch (e: Exception) {
            logger.error("Unexpected error while finding all tasks", e)
            Result.failure(e)
        }
    }

    fun findTaskById(id: Long): Result<Task> {
        return try {
            val task = taskRepository.findTaskById(id)
            if (task != null) {
                Result.success(task)
            } else {
                Result.failure(TaskNotFoundException(id))
            }
        } catch (e: DataAccessException) {
            logger.error("Database error while finding task with id: $id", e)
            Result.failure(TaskServiceException("Failed to retrieve task", e))
        } catch (e: Exception) {
            logger.error("Unexpected error while finding task with id: $id", e)
            Result.failure(e)
        }
    }

    fun update(req: UpdateTaskRequest): Result<Task> {
        return try {
            val task = taskRepository.findTaskById(req.id)
            if (task != null) {
                val updatedTodo = task.copy(
                    title = req.title,
                    description = req.description,
                    isCompleted = req.isCompleted
                )
                val result = taskRepository.save(updatedTodo)
                Result.success(result)
            } else {
                Result.failure(TaskNotFoundException(req.id))
            }
        } catch (e: DataAccessException) {
            logger.error("Database error while updating task with id: ${req.id}", e)
            Result.failure(TaskServiceException("Failed to update task", e))
        } catch (e: Exception) {
            logger.error("Unexpected error while updating task with id: ${req.id}", e)
            Result.failure(e)
        }
    }

    fun updateStatus(req: UpdateTaskStatusRequest): Result<Unit> {
        return try {
            val task = taskRepository.findTaskById(req.id)
            if (task != null) {
                val updatedTodo = task.copy(
                    isCompleted = req.isCompleted
                )
                taskRepository.save(updatedTodo)
                Result.success(Unit)
            } else {
                Result.failure(TaskNotFoundException(req.id))
            }
        } catch (e: DataAccessException) {
            logger.error("Database error while updating task with id: ${req.id}", e)
            Result.failure(TaskServiceException("Failed to update task", e))
        } catch (e: Exception) {
            logger.error("Unexpected error while updating task with id: ${req.id}", e)
            Result.failure(e)
        }
    }
}