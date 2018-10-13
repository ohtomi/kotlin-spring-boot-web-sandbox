package com.github.ohtomi.demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@SpringBootApplication
class DemoApplication {

    @RequestMapping(value = ["/"])
    fun handle(): String {
        return "hello"
    }
}

fun main(args: Array<String>) {
    runApplication<DemoApplication>(*args)
}
