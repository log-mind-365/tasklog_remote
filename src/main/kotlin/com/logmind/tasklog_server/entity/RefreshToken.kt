package com.logmind.tasklog_server.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "refresh_tokens")
data class RefreshToken(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "token", unique = true, nullable = false)
    val token: String,

    @Column(name = "user_email", nullable = false)
    val userEmail: String,

    @Column(name = "expires_at", nullable = false)
    val expiresAt: LocalDateTime,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "revoked", nullable = false)
    val revoked: Boolean = false
)