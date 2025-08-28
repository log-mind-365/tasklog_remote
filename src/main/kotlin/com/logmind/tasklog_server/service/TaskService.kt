package com.logmind.tasklog_server.service

import com.logmind.tasklog_server.dto.request.AddTaskRequest
import com.logmind.tasklog_server.dto.request.UpdateTaskRequest
import com.logmind.tasklog_server.entity.Task
import com.logmind.tasklog_server.exception.TaskNotFoundException
import com.logmind.tasklog_server.exception.TaskServiceException
import com.logmind.tasklog_server.repository.TaskRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TaskService(private val taskRepository: TaskRepository) {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    fun save(req: AddTaskRequest): Task {
        return execute("save task") {
            val newTask = Task(title = req.title, description = req.description)
            taskRepository.save(newTask)
        }
    }

    @Transactional
    fun delete(id: Long) {
        execute("delete task by id") {
            if (!taskRepository.existsById(id)) {
                throw TaskNotFoundException()
            }
            taskRepository.deleteById(id)
        }
    }

    fun findAll(): List<Task> {
        return execute("find all tasks") {
            taskRepository.findAll()
        }
    }

    fun findTaskById(id: Long): Task {
        return execute("find task by id") {
            taskRepository.findTaskById(id) ?: throw TaskNotFoundException()
        }
    }

    @Transactional
    fun update(req: UpdateTaskRequest): Task {
        val (id, title, description, isCompleted) = req
        return execute("update task") {
            taskRepository.findTaskById(id)?.let {
                val updatedTask = it.copy(
                    title = title ?: it.title,
                    description = description ?: it.description,
                    isCompleted = isCompleted ?: it.isCompleted
                )
                taskRepository.save(updatedTask)
            } ?: throw TaskNotFoundException()
        }
    }

    private inline fun <T> execute(operation: String, block: () -> T): T {
        return runCatching {
            block()
        }.getOrElse {
            logger.error("Failed execute $operation - ${it.message}")
            when (it) {
                is TaskNotFoundException -> throw it
                else -> throw TaskServiceException("Failed execute $operation", it)
            }
        }
    }
}