package com.logmind.tasklog_server.repository

import com.logmind.tasklog_server.entity.Task
import com.logmind.tasklog_server.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface TaskRepository : JpaRepository<Task, Long> {
    fun findTaskById(id: Long): Task?
    fun findAllByUser(user: User): List<Task>
    fun findByIdAndUser(id: Long, user: User): Task?
}