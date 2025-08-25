package com.logmind.tasklog_server.repository

import com.logmind.tasklog_server.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findByUsername(username: String): User?
    fun findByEmail(email: String): User?
    fun findByEmailAndPassword(email: String, password: String): User?
}