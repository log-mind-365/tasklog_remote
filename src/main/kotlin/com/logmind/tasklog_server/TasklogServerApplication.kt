package com.logmind.tasklog_server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TasklogServerApplication

fun main(args: Array<String>) {
    runApplication<TasklogServerApplication>(*args)
}
