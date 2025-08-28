package com.logmind.tasklog_server.repository

import com.logmind.tasklog_server.entity.Task
import org.springframework.data.jpa.repository.JpaRepository

interface TaskRepository : JpaRepository<Task, Long> {
    fun findTaskById(id: Long): Task?
}