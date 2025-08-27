package com.logmind.tasklog_server.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant

@Entity
@Table(name = "tasks")
data class Task(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val title: String,

    @Column
    val description: String? = null,

    @Column
    val isCompleted: Boolean = false,

    @CreationTimestamp
    @Column(updatable = false)
    val createdAt: Instant? = null,

    @UpdateTimestamp
    @Column
    val updatedAt: Instant? = null
)