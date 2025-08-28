package com.logmind.tasklog_server.service

import com.logmind.tasklog_server.dto.request.AddTaskRequest
import com.logmind.tasklog_server.dto.request.UpdateTaskRequest
import com.logmind.tasklog_server.entity.Task
import com.logmind.tasklog_server.entity.User
import com.logmind.tasklog_server.exception.TaskNotFoundException
import com.logmind.tasklog_server.exception.TaskServiceException
import com.logmind.tasklog_server.repository.TaskRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TaskService(private val taskRepository: TaskRepository) {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Transactional
    fun save(req: AddTaskRequest, user: User): Task {
        return execute("save task") {
            val newTask = Task(title = req.title, description = req.description, user = user)
            taskRepository.save(newTask)
        }
    }

    @Transactional
    fun delete(id: Long, user: User) {
        execute("delete task by id") {
            val task = taskRepository.findByIdAndUser(id, user) 
                ?: throw TaskNotFoundException()
            taskRepository.delete(task)
        }
    }

    @Transactional(readOnly = true)
    fun findAllByUser(user: User): List<Task> {
        return execute("find all tasks by user") {
            taskRepository.findAllByUser(user)
        }
    }

    @Transactional(readOnly = true)
    fun findTaskByIdAndUser(id: Long, user: User): Task {
        return execute("find task by id and user") {
            taskRepository.findByIdAndUser(id, user) ?: throw TaskNotFoundException()
        }
    }

    @Transactional
    fun update(req: UpdateTaskRequest, user: User): Task {
        val (id, title, description, isCompleted) = req
        return execute("update task") {
            taskRepository.findByIdAndUser(id, user)?.let {
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