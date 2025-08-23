package com.logmind.tasklog_server.controller

import com.logmind.tasklog_server.dto.request.AddTaskRequest
import com.logmind.tasklog_server.dto.request.UpdateTaskRequest
import com.logmind.tasklog_server.dto.request.UpdateTaskStatusRequest
import com.logmind.tasklog_server.entity.Task
import com.logmind.tasklog_server.service.TaskNotFoundException
import com.logmind.tasklog_server.service.TaskService
import com.logmind.tasklog_server.service.TaskServiceException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RequestMapping("/tasks")
@RestController
class TaskController(val taskService: TaskService) {

    @PostMapping
    fun addTask(@RequestBody req: AddTaskRequest): ResponseEntity<Task> {
        return taskService.save(req).fold(
            onSuccess = { ResponseEntity.ok(it) },
            onFailure = { handleFailure(it) }
        )
    }

    @GetMapping
    fun getAllTasks(): ResponseEntity<List<Task>> {
        return taskService.findAll().fold(
            onSuccess = { ResponseEntity.ok(it) },
            onFailure = { handleFailure(it) }
        )
    }

    @GetMapping("/{id}")
    fun getTaskById(@PathVariable id: Long): ResponseEntity<Task> {
        return taskService.findTaskById(id).fold(
            onSuccess = { ResponseEntity.ok(it) },
            onFailure = { handleFailure(it) }
        )
    }

    @DeleteMapping("/{id}")
    fun deleteTaskById(@PathVariable id: Long): ResponseEntity<Unit> {
        return taskService.delete(id).fold(
            onSuccess = { ResponseEntity.noContent().build() },
            onFailure = { handleFailure(it) }
        )
    }

    @PutMapping("/{id}")
    fun updateTask(
        @PathVariable id: Long,
        @RequestBody req: UpdateTaskRequest
    ): ResponseEntity<Task> {
        val updateReq = req.copy(id = id)
        return taskService.update(updateReq).fold(
            onSuccess = { ResponseEntity.ok(it) },
            onFailure = { handleFailure(it) }
        )
    }

    @PostMapping("/{id}")
    fun updateTaskStatus(
        @PathVariable id: Long,
        @RequestBody isCompleted: Boolean
    ): ResponseEntity<Task> {
        return taskService.updateStatus(UpdateTaskStatusRequest(id = id, isCompleted = isCompleted))
            .fold(
                onSuccess = { ResponseEntity.noContent().build() },
                onFailure = { handleFailure(it) }
            )
    }

    private fun <T> handleFailure(exception: Throwable): ResponseEntity<T> {
        return when (exception) {
            is TaskNotFoundException -> ResponseEntity.notFound().build()
            is TaskServiceException -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build()

            else -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }
}