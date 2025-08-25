package com.logmind.tasklog_server.config.jwt

import com.logmind.tasklog_server.entity.User
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.security.Keys
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.Duration
import java.util.*

@SpringBootTest
class `TokenProvider 테스트` {

    @Autowired
    private lateinit var tokenProvider: TokenProvider

    @Autowired
    private lateinit var jwtProperties: JwtProperties

    @Test
    fun `유효한 토큰을 생성할 수 있다`() {
        val testUser = User(
            id = 1L,
            email = "test@example.com",
            password = "password123"
        )

        val token = tokenProvider.generateToken(testUser, Duration.ofDays(14))

        assertNotNull(token)
        assertTrue(token.isNotEmpty())
        assertTrue(tokenProvider.validToken(token))
    }

    @Test
    fun `토큰에서 올바른 유저 ID를 추출할 수 있다`() {
        val testUser = User(
            id = 1L,
            email = "test@example.com",
            password = "password123"
        )

        val token = tokenProvider.generateToken(testUser, Duration.ofDays(14))
        val userId = tokenProvider.getUserId(token)

        assertEquals(testUser.id, userId)
    }

    @Test
    fun `토큰에서 올바른 이메일을 추출할 수 있다`() {
        val testUser = User(
            id = 1L,
            email = "test@example.com",
            password = "password123"
        )

        val token = tokenProvider.generateToken(testUser, Duration.ofDays(14))
        val claims = tokenProvider.getClaims(token)

        assertEquals(testUser.email, claims.subject)
    }

    @Test
    fun `토큰에서 Authentication 객체를 생성할 수 있다`() {
        val testUser = User(
            id = 1L,
            email = "test@example.com",
            password = "password123"
        )

        val token = tokenProvider.generateToken(testUser, Duration.ofDays(14))
        val authentication = tokenProvider.getAuthentication(token)

        assertNotNull(authentication)
        assertEquals(testUser.email, authentication.name)
        assertEquals(token, authentication.credentials)
        assertEquals(1, authentication.authorities.size)
        assertEquals("ROLE_USER", authentication.authorities.first().authority)
    }

    @Test
    fun `시간이 지나면 토큰이 만료된다`() {
        val testUser = User(
            id = 1L,
            email = "test@example.com",
            password = "password123"
        )

        val shortDuration = Duration.ofMillis(100)
        val token = tokenProvider.generateToken(testUser, shortDuration)

        Thread.sleep(200)
        assertFalse(tokenProvider.validToken(token))
    }

    @Test
    fun `빈 토큰은 유효하지 않다`() {
        assertFalse(tokenProvider.validToken(""))
    }

    @Test
    fun `잘못된 형식의 토큰은 유효하지 않다`() {
        val invalidToken = "invalid.jwt.token"
        assertFalse(tokenProvider.validToken(invalidToken))
    }

    @Test
    fun `잘못된 서명의 토큰은 유효하지 않다`() {
        val wrongSecretToken = Jwts.builder()
            .setSubject("test@example.com")
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + 3600000))
            .signWith(Keys.hmacShaKeyFor("wrong-secret-key-for-testing-purposes".toByteArray()))
            .compact()

        assertFalse(tokenProvider.validToken(wrongSecretToken))
    }

    @Test
    fun `만료된 토큰으로 Claims를 가져오면 ExpiredJwtException이 발생한다`() {
        val expiredToken = Jwts.builder()
            .setSubject("test@example.com")
            .setIssuedAt(Date(System.currentTimeMillis() - 7200000))
            .setExpiration(Date(System.currentTimeMillis() - 3600000))
            .signWith(Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray()))
            .compact()

        assertThrows<ExpiredJwtException> {
            tokenProvider.getClaims(expiredToken)
        }
    }

    @Test
    fun `잘못된 형식의 토큰으로 Claims를 가져오면 MalformedJwtException이 발생한다`() {
        val malformedToken = "this.is.malformed"

        assertThrows<MalformedJwtException> {
            tokenProvider.getClaims(malformedToken)
        }
    }

    @Test
    fun `getUserId로 올바른 사용자 ID를 반환한다`() {
        val testUser = User(
            id = 123L,
            email = "user@test.com",
            password = "testpassword"
        )
        val token = tokenProvider.generateToken(testUser, Duration.ofHours(1))

        val userId = tokenProvider.getUserId(token)

        assertEquals(testUser.id, userId)
    }

    @Test
    fun `getAuthentication으로 올바른 Authentication 객체를 반환한다`() {
        val testUser = User(
            id = 123L,
            email = "user@test.com",
            password = "testpassword"
        )
        val token = tokenProvider.generateToken(testUser, Duration.ofHours(1))

        val authentication = tokenProvider.getAuthentication(token)

        assertNotNull(authentication)
        assertEquals(testUser.email, authentication.name)
        assertEquals(token, authentication.credentials)
        assertEquals(1, authentication.authorities.size)
        assertEquals("ROLE_USER", authentication.authorities.first().authority)
    }
}