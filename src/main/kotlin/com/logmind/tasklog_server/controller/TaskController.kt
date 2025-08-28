package com.logmind.tasklog_server.controller

import com.logmind.tasklog_server.dto.request.AddTaskRequest
import com.logmind.tasklog_server.dto.request.UpdateTaskRequest
import com.logmind.tasklog_server.entity.Task
import com.logmind.tasklog_server.service.TaskService
import org.springframework.web.bind.annotation.*

@RequestMapping("/api/tasks")
@RestController
class TaskController(private val taskService: TaskService) {

    @PostMapping
    fun addTask(@RequestBody req: AddTaskRequest): Task {
        return taskService.save(req)
    }

    @GetMapping
    fun getAllTasks(): List<Task> {
        return taskService.findAll()
    }

    @GetMapping("/{id}")
    fun getTaskById(@PathVariable id: Long): Task {
        return taskService.findTaskById(id)
    }

    @DeleteMapping("/{id}")
    fun deleteTaskById(@PathVariable id: Long) {
        taskService.delete(id)
    }

    @PatchMapping("/{id}")
    fun updateTask(
        @PathVariable id: Long,
        @RequestBody req: UpdateTaskRequest
    ): Task {
        val updateReq = req.copy(id = id)
        return taskService.update(updateReq)
    }
}