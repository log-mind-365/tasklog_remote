package com.logmind.tasklog_server.service

import com.logmind.tasklog_server.dto.request.AddTaskRequest
import com.logmind.tasklog_server.dto.request.UpdateTaskRequest
import com.logmind.tasklog_server.dto.request.UpdateTaskStatusRequest
import com.logmind.tasklog_server.entity.Task
import com.logmind.tasklog_server.exception.TaskNotFoundException
import com.logmind.tasklog_server.exception.TaskServiceException
import com.logmind.tasklog_server.repository.TaskRepository
import org.slf4j.LoggerFactory
import org.springframework.dao.DataAccessException
import org.springframework.stereotype.Service

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
            Result.failure(TaskServiceException("Database error while saving task", e))
        } catch (e: Exception) {
            Result.failure(TaskServiceException("Unexpected error while saving task", e))
        }
    }

    fun delete(id: Long): Result<Boolean> {
        return try {
            taskRepository.deleteTaskById(id)
            Result.success(true)
        } catch (e: DataAccessException) {
            Result.failure(
                TaskServiceException(
                    "Database error while deleting task with id: $id", e
                )
            )
        } catch (e: Exception) {
            Result.failure(
                TaskServiceException(
                    "Unexpected error while deleting task with id: $id", e
                )
            )
        }
    }

    fun findAll(): Result<List<Task>> {
        return try {
            val tasks = taskRepository.findAll()
            Result.success(tasks)
        } catch (e: DataAccessException) {
            Result.failure(TaskServiceException("Database error while finding all tasks", e))
        } catch (e: Exception) {
            Result.failure(TaskServiceException("Unexpected error while finding all tasks", e))
        }
    }

    fun findTaskById(id: Long): Result<Task> {
        return try {
            val task = taskRepository.findTaskById(id)
            if (task != null) {
                Result.success(task)
            } else {
                Result.failure(TaskNotFoundException())
            }
        } catch (e: DataAccessException) {
            Result.failure(
                TaskServiceException(
                    "Database error while finding task with id: $id", e
                )
            )
        } catch (e: Exception) {
            Result.failure(
                TaskServiceException(
                    "Unexpected error while finding task with id: $id", e
                )
            )
        }
    }

    fun update(req: UpdateTaskRequest): Result<Task> {
        return try {
            val task = taskRepository.findTaskById(req.id)
            if (task != null) {
                val updatedTodo = task.copy(
                    title = req.title, description = req.description, isCompleted = req.isCompleted
                )
                val result = taskRepository.save(updatedTodo)
                Result.success(result)
            } else {
                Result.failure(TaskNotFoundException())
            }
        } catch (e: DataAccessException) {
            Result.failure(
                TaskServiceException(
                    "Database error while updating task with id: ${req.id}", e
                )
            )
        } catch (e: Exception) {
            Result.failure(
                TaskServiceException(
                    "Unexpected error while updating task with id: ${req.id}", e
                )
            )
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
                Result.failure(TaskNotFoundException())
            }
        } catch (e: DataAccessException) {
            Result.failure(
                TaskServiceException(
                    "Database error while updating task with id: ${req.id}", e
                )
            )
        } catch (e: Exception) {
            Result.failure(
                TaskServiceException(
                    "Unexpected error while updating task with id: ${req.id}", e
                )
            )
        }
    }
}