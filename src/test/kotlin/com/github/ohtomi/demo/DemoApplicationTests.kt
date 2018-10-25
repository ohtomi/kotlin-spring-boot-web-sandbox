package com.github.ohtomi.demo

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.core.io.ClassPathResource
import org.springframework.http.*
import org.springframework.http.client.ClientHttpResponse
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.ResponseExtractor
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.file.Files

@ActiveProfiles(value = ["test"])
@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DemoApplicationTests {

    @Autowired
    lateinit var template: TestRestTemplate

    // handle GET request ------------------------------------------------------

    @Test
    fun testGetText() {

        val response = template.getForEntity("${template.rootUri}/get/response.text", String::class.java)

        val fixture = mapOf(
                "status" to 200,
                "contentType" to MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8),
                "body" to "/get/response.text\n----\nkotlin-spring-boot-web-sandbox"
        )
        Assert.assertEquals(fixture["status"], response.statusCodeValue)
        Assert.assertEquals(fixture["contentType"], response.headers.contentType)
        Assert.assertEquals(fixture["body"], response.body)
    }

    @Test
    fun getGetJson() {

        val response = template.getForEntity("${template.rootUri}/get/response.json", Map::class.java)

        val fixture = mapOf(
                "status" to 200,
                "contentType" to MediaType.APPLICATION_JSON_UTF8,
                "body" to mapOf(
                        "name" to "kotlin-spring-boot-web-sandbox",
                        "group" to "com.github.ohtomi",
                        "version" to "0.0.1-SNAPSHOT"
                )
        )
        Assert.assertEquals(fixture["status"], response.statusCodeValue)
        Assert.assertEquals(fixture["contentType"], response.headers.contentType)
        Assert.assertEquals(fixture["body"], response.body)
    }

    @Test
    fun testGetHtml() {

        val response = template.getForEntity("${template.rootUri}/get/response.html", String::class.java)

        val fixture = mapOf(
                "status" to 200,
                "contentType" to MediaType.TEXT_HTML,
                "body" to loadFromFile(ClassPathResource("static/get/response.html"))
        )
        Assert.assertEquals(fixture["status"], response.statusCodeValue)
        Assert.assertEquals(fixture["contentType"], response.headers.contentType)
        Assert.assertEquals(fixture["body"], response.body)
    }

    // handle POST request -----------------------------------------------------

    @Test
    fun testPostJson() {

        val form = mapOf("name" to "ふが", "age" to 19)
        val response = template.postForEntity("${template.rootUri}/post/json", form, Map::class.java)

        val fixture = mapOf(
                "status" to 200,
                "contentType" to MediaType.APPLICATION_JSON_UTF8,
                "body" to form.toMutableMap().apply { put("application", "kotlin-spring-boot-web-sandbox") }
        )
        Assert.assertEquals(fixture["status"], response.statusCodeValue)
        Assert.assertEquals(fixture["contentType"], response.headers.contentType)
        Assert.assertEquals(fixture["body"], response.body)
    }

    @Test
    fun testPostFormUrlEncoded() {

        val form = mapOf("name" to "ふが", "age" to 19)
        val request = HttpEntity(
                form.entries.map { "${it.key}=${it.value}" }.joinToString("&"),
                HttpHeaders().apply { set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE) }
        )
        val response = template.postForEntity("${template.rootUri}/post/form-urlencoded", request, Map::class.java)

        val fixture = mapOf(
                "status" to 200,
                "contentType" to MediaType.APPLICATION_JSON_UTF8,
                "body" to form
                        .mapValues { it.value.toString() }.toMutableMap() // 19 => "19"
                        .apply { put("application", "kotlin-spring-boot-web-sandbox") }
        )
        Assert.assertEquals(fixture["status"], response.statusCodeValue)
        Assert.assertEquals(fixture["contentType"], response.headers.contentType)
        Assert.assertEquals(fixture["body"], response.body)
        Assert.assertTrue(response.body!!["age"] is String)
    }

    @Test
    fun testPostMultipartFormData() {

        val form = mapOf("name" to listOf("ふが"), "age" to listOf(19))
        val request = HttpEntity(
                LinkedMultiValueMap(form),
                HttpHeaders().apply { set(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE) }
        )
        val response = template.postForEntity("${template.rootUri}/post/multipart-form-data", request, Map::class.java)

        val fixture = mapOf(
                "status" to 200,
                "contentType" to MediaType.APPLICATION_JSON_UTF8,
                "body" to form
                        .mapValues { it.value.first() }.toMutableMap()
                        .apply { put("application", "kotlin-spring-boot-web-sandbox") }
        )
        Assert.assertEquals(fixture["status"], response.statusCodeValue)
        Assert.assertEquals(fixture["contentType"], response.headers.contentType)
        Assert.assertEquals(fixture["body"], response.body)
    }

    // handle FILE uploading, FILE downloading ---------------------------------

    @Test
    fun testUploadFile() {

        val resource = ClassPathResource("application.yml")
        val form = mapOf("file" to listOf(resource))
        val request = HttpEntity(
                LinkedMultiValueMap(form),
                HttpHeaders().apply { set(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE) }
        )
        val response = template.postForEntity("${template.rootUri}/file/upload", request, Map::class.java)

        val fixture = mapOf(
                "status" to 200,
                "contentType" to MediaType.APPLICATION_JSON_UTF8,
                "body" to mapOf(
                        "filename" to resource.filename,
                        "contentType" to MediaType.APPLICATION_OCTET_STREAM_VALUE,
                        "size" to resource.contentLength().toInt()) // Long => Int
        )
        Assert.assertEquals(fixture["status"], response.statusCodeValue)
        Assert.assertEquals(fixture["contentType"], response.headers.contentType)
        Assert.assertEquals(fixture["body"], response.body)
        Assert.assertTrue(response.body!!["size"] is Int)
    }

    @Test
    fun testDownloadFile() {

        val resource = ClassPathResource("application.yml")
        val extractor = DemoDownloadResponseExtractor()
        val response = template.execute("${template.rootUri}/file/download", HttpMethod.GET, null, extractor)

        val fixture = mapOf(
                "status" to 200,
                "contentType" to MediaType.APPLICATION_OCTET_STREAM,
                "body" to loadFromFile(resource)
        )
        Assert.assertEquals(fixture["status"], response.statusCodeValue)
        Assert.assertEquals(fixture["contentType"], response.headers.contentType)
        Assert.assertEquals(fixture["body"], response.body)
    }

    class DemoDownloadResponseExtractor : ResponseExtractor<ResponseEntity<String>> {

        override fun extractData(response: ClientHttpResponse): ResponseEntity<String>? =
                ResponseEntity
                        .status(response.statusCode)
                        .headers(response.headers)
                        .body(response.body.reader(StandardCharsets.UTF_8).readText())
    }
}

// helper ------------------------------------------------------------------

fun loadFromFile(resource: ClassPathResource, charset: Charset = StandardCharsets.UTF_8): String =
        String(Files.readAllBytes(resource.file.toPath()), charset)
