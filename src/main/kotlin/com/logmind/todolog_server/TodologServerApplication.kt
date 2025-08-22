package com.logmind.todolog_server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TodologServerApplication

fun main(args: Array<String>) {
    runApplication<TodologServerApplication>(*args)
}
