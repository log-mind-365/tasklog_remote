package com.logmind.tasklog_server.config.jwt

import io.jsonwebtoken.Header
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import java.time.Duration
import java.util.*

data class JwtFactory(
    val subject: String = "test@email.com",
    val issuedAt: Date = Date(),
    val expiration: Date = Date(Date().time + Duration.ofDays(14).toMillis()),
    val claims: Map<String, Any> = emptyMap()
) {
    companion object {
        fun withDefaultValues(): JwtFactory = JwtFactory()
    }

    fun createToken(jwtProperties: JwtProperties): String {
        return Jwts.builder().setSubject(subject)
            .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
            .setIssuer(jwtProperties.issuer)
            .setIssuedAt(issuedAt)
            .setExpiration(expiration)
            .addClaims(claims)
            .signWith(Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray()))
            .compact()
    }
}