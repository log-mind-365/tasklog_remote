package com.logmind.tasklog_server.controller

import com.logmind.tasklog_server.dto.request.AddTaskRequest
import com.logmind.tasklog_server.dto.request.UpdateTaskRequest
import com.logmind.tasklog_server.entity.Task
import com.logmind.tasklog_server.entity.User
import com.logmind.tasklog_server.service.TaskService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RequestMapping("/api/tasks")
@RestController
class TaskController(private val taskService: TaskService) {

    @PostMapping
    fun addTask(
        @RequestBody req: AddTaskRequest,
        @AuthenticationPrincipal user: User
    ): Task {
        return taskService.save(req, user)
    }

    @GetMapping
    fun getAllTasks(@AuthenticationPrincipal user: User): List<Task> {
        return taskService.findAllByUser(user)
    }

    @GetMapping("/{id}")
    fun getTaskById(
        @PathVariable id: Long,
        @AuthenticationPrincipal user: User
    ): Task {
        return taskService.findTaskByIdAndUser(id, user)
    }

    @DeleteMapping("/{id}")
    fun deleteTaskById(
        @PathVariable id: Long,
        @AuthenticationPrincipal user: User
    ) {
        taskService.delete(id, user)
    }

    @PatchMapping("/{id}")
    fun updateTask(
        @PathVariable id: Long,
        @RequestBody req: UpdateTaskRequest,
        @AuthenticationPrincipal user: User
    ): Task {
        val updateReq = req.copy(id = id)
        return taskService.update(updateReq, user)
    }
}