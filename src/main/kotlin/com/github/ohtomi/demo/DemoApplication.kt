package com.github.ohtomi.demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.zuul.EnableZuulProxy
import org.springframework.cloud.netflix.zuul.filters.post.LocationRewriteFilter
import org.springframework.context.annotation.Bean
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@EnableZuulProxy
@RestController
@SpringBootApplication
class DemoApplication {

    @RequestMapping(value = ["/greet"])
    fun handle(): String {
        return "hello"
    }

    @Bean
    fun locationRewriteFilter(): LocationRewriteFilter {
        return LocationRewriteFilter()
    }
}

fun main(args: Array<String>) {
    runApplication<DemoApplication>(*args)
}
