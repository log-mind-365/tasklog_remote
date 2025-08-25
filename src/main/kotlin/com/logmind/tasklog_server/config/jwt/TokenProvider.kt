package com.logmind.tasklog_server.config.jwt

import com.logmind.tasklog_server.entity.User
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Header
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.*

@Service
class TokenProvider(private val jwtProperties: JwtProperties) {
    fun generateToken(user: User, expiredAt: Duration): String {
        val now = Date()
        return makeToken(Date(now.time + expiredAt.toMillis()), user)
    }

    private fun makeToken(expiry: Date, user: User): String {
        val now = Date()
        return Jwts.builder()
            .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
            .setIssuer(jwtProperties.issuer)
            .setIssuedAt(now)
            .setExpiration(expiry)
            .setSubject(user.email)
            .claim("id", user.id)
            .signWith(Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray()))
            .compact()
    }

    fun validToken(token: String): Boolean {
        try {
            Jwts.parserBuilder()
                .setSigningKey(jwtProperties.secret.toByteArray())
                .build()
                .parseClaimsJws(token)
            return true
        } catch (e: Exception) {
            return false
        }
    }

    fun getAuthentication(token: String): Authentication {
        val claims = getClaims(token)
        val authorities = Collections.singleton(SimpleGrantedAuthority("ROLE_USER"))
        return UsernamePasswordAuthenticationToken(
            org.springframework.security.core.userdetails.User(
                claims.subject,
                "",
                authorities
            ), token, authorities
        )
    }

    fun getUserId(token: String): Long {
        val claims = getClaims(token)
        return claims.get("id", Integer::class.java).toLong()
    }

    fun getClaims(token: String): Claims {
        return Jwts.parserBuilder().setSigningKey(jwtProperties.secret.toByteArray()).build()
            .parseClaimsJws(token).body
    }
}