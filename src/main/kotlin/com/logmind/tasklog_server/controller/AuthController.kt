package com.logmind.tasklog_server.controller

import com.logmind.tasklog_server.dto.request.LoginRequest
import com.logmind.tasklog_server.dto.request.RegisterRequest
import com.logmind.tasklog_server.dto.response.AuthResponse
import com.logmind.tasklog_server.service.AuthService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/auth")
@RestController
class AuthController(
    private val authService: AuthService
) {
    @PostMapping("/join")
    fun registerUser(@RequestBody registerRequest: RegisterRequest): AuthResponse {
        return authService.register(registerRequest)
    }

    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest): AuthResponse {
        return authService.login(loginRequest)
    }

    @PostMapping("/logout")
    fun logout(request: HttpServletRequest, response: HttpServletResponse) {
        authService.logout(request, response)
    }
}