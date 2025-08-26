package com.logmind.tasklog_server.service

import com.logmind.tasklog_server.dto.request.LoginRequest
import com.logmind.tasklog_server.dto.request.RegisterRequest
import com.logmind.tasklog_server.dto.response.AuthResponse
import com.logmind.tasklog_server.entity.User
import com.logmind.tasklog_server.repository.UserRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.crypto.password.PasswordEncoder
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExtendWith(MockitoExtension::class)
class AuthServiceTest {

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var passwordEncoder: PasswordEncoder

    @Mock
    private lateinit var tokenService: TokenService

    @Mock
    private lateinit var authenticationManager: AuthenticationManager

    @InjectMocks
    private lateinit var authService: AuthService

    val registerRequest = RegisterRequest(
        email = "test@email.com",
        password = "plainPassword",
        username = "testUser"
    )
    val encodedPassword = "encodedPassword"
    val savedUser = User(
        id = 1L,
        email = registerRequest.email,
        password = encodedPassword,
        username = registerRequest.username
    )

    @Test
    fun `회원가입이 성공적으로 동작한다`() {
        // Given
        `when`(userRepository.findByEmail(registerRequest.email)).thenReturn(null)
        // 이메일 중복 통과 가정
        `when`(passwordEncoder.encode(registerRequest.password)).thenReturn(encodedPassword)
        // 인코딩된 비밀번호 반환 가정
        `when`(userRepository.save(any(User::class.java))).thenReturn(savedUser)
        // 유저 저장 성공 가정

        // When
        val result = authService.register(registerRequest)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(savedUser, result.getOrNull())
        verify(userRepository).findByEmail(registerRequest.email)
        verify(passwordEncoder).encode(registerRequest.password)
        verify(userRepository).save(any(User::class.java))
    }

    @Test
    fun `이메일이 이미 존재하면 회원가입이 실패한다`() {
        // Given
        val existingUser = User(
            id = 1L,
            email = registerRequest.email,
            password = "existingPassword",
            username = "existingUser"
        )
        `when`(userRepository.findByEmail(registerRequest.email)).thenReturn(existingUser)

        // When
        val result = authService.register(registerRequest)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Email is already registered", result.exceptionOrNull()?.message)
        verify(userRepository).findByEmail(registerRequest.email)
        verify(passwordEncoder, never()).encode(any())
        verify(userRepository, never()).save(any(User::class.java))
    }

    @Test
    fun `DB 저장 실패시 예외가 발생한다`() {
        // Given
        `when`(userRepository.findByEmail(registerRequest.email)).thenReturn(null)
        `when`(passwordEncoder.encode(registerRequest.password)).thenReturn("encodedPassword")
        `when`(userRepository.save(any(User::class.java))).thenThrow(RuntimeException("DB Error"))

        // When
        val result = authService.register(registerRequest)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Register failed", result.exceptionOrNull()?.message)
    }

    @Test
    fun `로그인이 성공적으로 동작한다`() {
        // Given
        val loginRequest = LoginRequest(
            email = registerRequest.email,
            password = registerRequest.password
        )
        // 사용자가 존재한다고 Mock 설정
        `when`(userRepository.findByEmail(loginRequest.email)).thenReturn(savedUser)
        // 패스워드 매칭이 성공한다고 Mock 설정
        `when`(passwordEncoder.matches(loginRequest.password, savedUser.password)).thenReturn(true)
        // 토큰 발급 Mock 설정
        `when`(tokenService.createToken(savedUser)).thenReturn(
            AuthResponse(
                accessToken = "accessToken",
                refreshToken = "refreshToken",
                expiresIn = 1000L,
                user = savedUser,
            )
        )

        // When
        val loginResult = authService.login(loginRequest)

        // Then
        assertTrue(loginResult.isSuccess)
        assertEquals(savedUser, loginResult.getOrNull()?.user)
        verify(userRepository).findByEmail(loginRequest.email)
        verify(passwordEncoder).matches(loginRequest.password, savedUser.password)
        verify(tokenService).createToken(savedUser)
    }

}