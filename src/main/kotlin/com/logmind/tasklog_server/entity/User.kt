package com.logmind.tasklog_server.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.Instant

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(unique = true, nullable = false)
    private val username: String,

    @Column(nullable = false)
    private val password: String,

    @Column(unique = true, nullable = false)
    val email: String,

    @Column(length = 10)
    val displayName: String? = null,

    @Column
    val profileImage: String? = null,

    @CreationTimestamp
    @Column(updatable = false)
    val createdAt: Instant? = null,

    @UpdateTimestamp
    @Column
    val updatedAt: Instant? = null,
) : UserDetails {
    override fun getAuthorities(): Collection<GrantedAuthority> {
        return listOf(SimpleGrantedAuthority("user"))
    }

    override fun getPassword(): String = password

    override fun getUsername(): String = username
}

data class LoginUserInfo(
    val id: Long,
    val email: String,
    val displayName: String?,
    val profileImage: String?,
    val createdAt: Instant,
    val updatedAt: Instant,
)

fun User.toLoginUserInfo(): LoginUserInfo {
    return LoginUserInfo(
        id = requireNotNull(this.id) { "id must not be null" },
        email = this.email,
        displayName = this.displayName,
        profileImage = this.profileImage,
        createdAt = requireNotNull(this.createdAt) { "createdAt must not be null" },
        updatedAt = requireNotNull(this.updatedAt) { "updatedAt must not be null" },
    )
}
