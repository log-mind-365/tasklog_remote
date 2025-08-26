package com.logmind.tasklog_server.config

import com.logmind.tasklog_server.security.jwt.JwtAuthenticationFilter
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.mockito.Mockito.mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@SpringJUnitConfig
class SecurityConfigTest {
    @Autowired
    private lateinit var securityConfig: SecurityConfig

    @MockitoBean
    private lateinit var userService: UserDetailsService

    @MockitoBean
    private lateinit var jwtAuthenticationFilter: JwtAuthenticationFilter

    @Test
    fun `BCryptPasswordEncoder Bean이 올바르게 생성되어야 한다`() {
        val encoder = securityConfig.bCryptPasswordEncoder()

        assertNotNull(encoder)

        val rawPassword = "testPassword"
        val encodedPassword = encoder.encode(rawPassword)

        assertNotEquals("testPassword", encodedPassword)
        assertTrue(encoder.matches(rawPassword, encodedPassword))
        assertFalse(encoder.matches("wrongPassword", encodedPassword))
    }

    @Test
    fun `AuthenticationManager Bean이 올바르게 생성되어야 한다`() {
        val authConfig = mock(AuthenticationConfiguration::class.java)
        val authManager = mock(AuthenticationManager::class.java)

        org.mockito.Mockito.`when`(authConfig.authenticationManager).thenReturn(authManager)

        val result = securityConfig.authenticationManager(authConfig)

        assertNotNull(result)
        assertEquals(authManager, result)
    }
}