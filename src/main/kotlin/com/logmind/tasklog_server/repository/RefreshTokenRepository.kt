package com.logmind.tasklog_server.repository

import com.logmind.tasklog_server.entity.RefreshToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface RefreshTokenRepository : JpaRepository<RefreshToken, Long> {
    fun findByToken(token: String): RefreshToken?
    
    fun findByUserEmail(userEmail: String): List<RefreshToken>
    
    @Modifying
    @Query("UPDATE RefreshToken r SET r.revoked = true WHERE r.userEmail = :userEmail")
    fun revokeAllUserTokens(@Param("userEmail") userEmail: String)
    
    @Modifying
    @Query("UPDATE RefreshToken r SET r.revoked = true WHERE r.token = :token")
    fun revokeToken(@Param("token") token: String)
    
    @Modifying
    @Query("DELETE FROM RefreshToken r WHERE r.expiresAt < :now")
    fun deleteExpiredTokens(@Param("now") now: LocalDateTime)
}