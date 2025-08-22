package com.logmind.todolog_server.repository

import com.logmind.todolog_server.entity.Task
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface TaskRepository : JpaRepository<Task, Long> {
    fun deleteTaskById(id: Long)
    fun findTaskById(id: Long): Task?
}