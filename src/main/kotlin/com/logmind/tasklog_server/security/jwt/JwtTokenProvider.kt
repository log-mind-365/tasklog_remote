package com.logmind.tasklog_server.security.jwt

import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*

@Component
class JwtTokenProvider(
    private val jwtProperties: JwtProperties
) {
    private val logger = LoggerFactory.getLogger(JwtTokenProvider::class.java)

    fun generateAccessToken(authentication: Authentication): String {
        val userPrincipal = authentication.principal as UserDetails
        val expiryDate = Date(Date().time + jwtProperties.accessTokenExpiration)

        return Jwts.builder()
            .setSubject(userPrincipal.username)
            .setIssuedAt(Date())
            .setExpiration(expiryDate)
            .setIssuer(jwtProperties.issuer)
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact()
    }

    fun generateRefreshToken(authentication: Authentication): String {
        val userPrincipal = authentication.principal as UserDetails
        val expiryDate = Date(Date().time + jwtProperties.refreshTokenExpiration)

        return Jwts.builder()
            .setSubject(userPrincipal.username)
            .setIssuedAt(Date())
            .setExpiration(expiryDate)
            .setIssuer(jwtProperties.issuer)
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact()
    }

    fun getUsernameFromToken(token: String): String {
        val claims = Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)

        return claims.body.subject
    }

    fun validateToken(token: String): Boolean {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
            return true
        } catch (ex: SecurityException) {
            logger.error("Invalid JWT signature")
            throw SecurityException("Invalid JWT signature")
        } catch (ex: MalformedJwtException) {
            logger.error("Invalid JWT token")
        } catch (ex: ExpiredJwtException) {
            logger.error("Expired JWT token")
        } catch (ex: UnsupportedJwtException) {
            logger.error("Unsupported JWT token")
        } catch (ex: IllegalArgumentException) {
            logger.error("JWT claims string is empty")
        }
        return false
    }

    private fun getSigningKey(): Key {
        val keyBytes = Decoders.BASE64.decode(jwtProperties.secret)
        return Keys.hmacShaKeyFor(keyBytes)
    }
}