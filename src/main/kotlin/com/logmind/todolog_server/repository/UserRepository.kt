package com.logmind.todolog_server.repository

import com.logmind.todolog_server.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
}