package com.github.ohtomi.demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.zuul.EnableZuulProxy
import org.springframework.cloud.netflix.zuul.filters.post.LocationRewriteFilter
import org.springframework.context.annotation.Bean
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@EnableZuulProxy
@RestController
@SpringBootApplication
class DemoApplication {

    // welcome page ------------------------------------------------------------

    @GetMapping(value = ["/index.html"], produces = [MediaType.TEXT_HTML_VALUE])
    fun index(): Resource {

        return ClassPathResource("static/index.html")
    }

    // handle GET request ------------------------------------------------------

    @GetMapping(value = ["/get/response.text"], produces = [MediaType.TEXT_PLAIN_VALUE])
    fun getText(): String {

        return "/get/response.text\n----\nkotlin-spring-boot-web-sandbox"
    }

    @GetMapping(value = ["/get/response.json"], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    fun getJson(): Map<String, Any> {

        return mapOf(
                "name" to "kotlin-spring-boot-web-sandbox",
                "group" to "com.github.ohtomi",
                "version" to "0.0.1-SNAPSHOT"
        )
    }

    @GetMapping(value = ["/get/response.html"], produces = [MediaType.TEXT_HTML_VALUE])
    fun getHtml(): Resource {

        return ClassPathResource("static/get/response.html")
    }

    // handle POST request -----------------------------------------------------

    /* application/json */
    @PostMapping(value = ["/post/json"],
            consumes = [MediaType.APPLICATION_JSON_UTF8_VALUE],
            produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    fun postJson(@RequestBody body: Map<String, Any>): Map<String, Any> {

        return body.toMutableMap().apply { put("application", "kotlin-spring-boot-web-sandbox") }
    }

    /* application/x-www-form-urlencoded */
    @PostMapping(value = ["/post/form-urlencoded"],
            consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE],
            produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    fun postFormUrlEncoded(@RequestParam body: Map<String, Any>): Map<String, Any> {

        return body.toMutableMap().apply { put("application", "kotlin-spring-boot-web-sandbox") }
    }

    /* multipart/form-data */
    @PostMapping(value = ["/post/multipart-form-data"],
            consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
            produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    fun postMultipartFormData(request: DemoMultipart): Map<String, Any> {

        val filename = if (request.file?.originalFilename != null) request.file!!.originalFilename!! else "<null>"
        return mapOf("application" to "kotlin-spring-boot-web-sandbox", "filename" to filename)
    }

    data class DemoMultipart(var name: String, var age: Int, var file: MultipartFile?)

    // handle FILE uploading, FILE downloading ---------------------------------

    // beans -------------------------------------------------------------------

    @Bean
    fun newLocationRewriteFilter(): LocationRewriteFilter {

        return LocationRewriteFilter()
    }
}

fun main(args: Array<String>) {

    runApplication<DemoApplication>(*args)
}
