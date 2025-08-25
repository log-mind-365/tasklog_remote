package com.logmind.tasklog_server.controller

import com.logmind.tasklog_server.dto.request.AddUserRequest
import com.logmind.tasklog_server.service.UserService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@Controller
class UserController(private val userService: UserService) {
    @PostMapping("/user")
    fun createUser(@RequestBody addUserRequest: AddUserRequest): String {
        userService.createUser(addUserRequest)
        return "redirect:/login"
    }

    @GetMapping("/logout")
    fun logout(request: HttpServletRequest, response: HttpServletResponse): String {
        SecurityContextLogoutHandler().logout(
            request,
            response,
            SecurityContextHolder.getContext().authentication
        )
        return "redirect:/login"
    }
}