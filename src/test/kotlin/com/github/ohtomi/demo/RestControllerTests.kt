package com.github.ohtomi.demo

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.core.io.ClassPathResource
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.util.LinkedMultiValueMap
import java.nio.charset.StandardCharsets

@RunWith(SpringRunner::class)
@WebMvcTest(RestControllerConfig::class)
class RestControllerTests {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    // handle GET request ------------------------------------------------------

    @Test
    fun testGetText() {

        mockMvc.perform {
            get("/get/response.text").run {
                buildRequest(it)
            }
        }
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8)))
                .andExpect(content().string("/get/response.text\n----\nkotlin-spring-boot-web-sandbox"))
    }

    @Test
    fun getGetJson() {

        mockMvc.perform {
            get("/get/response.json").run {
                buildRequest(it)
            }
        }
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json(objectMapper.writeValueAsString(mapOf(
                        "name" to "kotlin-spring-boot-web-sandbox",
                        "group" to "com.github.ohtomi",
                        "version" to "0.0.1-SNAPSHOT"
                ))))
    }

    @Test
    fun testGetHtml() {

        mockMvc.perform {
            get("/get/response.html").run {
                buildRequest(it)
            }
        }
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.TEXT_HTML))
                .andExpect(content().string(loadFromFile(ClassPathResource("static/get/response.html"))))
    }

    // handle POST request -----------------------------------------------------

    @Test
    fun testPostJson() {

        val request = mapOf("name" to "ふが", "age" to 19)

        mockMvc.perform {
            post("/post/json").run {
                contentType(MediaType.APPLICATION_JSON_UTF8)
                content(objectMapper.writeValueAsBytes(request))
                buildRequest(it)
            }
        }
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json(objectMapper.writeValueAsString(
                        request.toMutableMap().apply { put("application", "kotlin-spring-boot-web-sandbox") }
                )))
    }

    @Test
    fun testPostFormUrlEncoded() {

        val form = mapOf("name" to "ふが", "age" to 19)
        val request = UrlEncodedFormEntity(
                form.entries.map { BasicNameValuePair(it.key, it.value.toString()) }, StandardCharsets.UTF_8)

        mockMvc.perform {
            post("/post/form-urlencoded").run {
                contentType(MediaType.APPLICATION_FORM_URLENCODED)
                content(EntityUtils.toString(request))
                buildRequest(it)
            }
        }
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json(objectMapper.writeValueAsString(
                        form
                                .mapValues { it.value.toString() }.toMutableMap() // 19 => "19"
                                .apply { put("application", "kotlin-spring-boot-web-sandbox") }
                )))
    }

    @Test
    fun testPostMultipartFormData() {

        val form = mapOf("name" to listOf("ふが"), "age" to listOf(19))
        val request = LinkedMultiValueMap(
                form.mapValues { it.value.map { it.toString() } })

        mockMvc.perform {
            multipart("/post/multipart-form-data").run {
                params(request)
                buildRequest(it)
            }
        }
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json(objectMapper.writeValueAsString(
                        form
                                .mapValues { it.value.first() }.toMutableMap()
                                .apply { put("application", "kotlin-spring-boot-web-sandbox") }
                )))
    }

    // handle FILE uploading, FILE downloading ---------------------------------

    @Test
    fun testUploadFile() {

        val resource = ClassPathResource("application.yml")
        val form = listOf(MockMultipartFile("file", resource.filename, MediaType.APPLICATION_OCTET_STREAM_VALUE, resource.inputStream))

        mockMvc.perform {
            multipart("/file/upload").run {
                form.forEach { f -> file(f) }
                buildRequest(it)
            }
        }
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json(objectMapper.writeValueAsString(
                        mapOf(
                                "filename" to resource.filename,
                                "contentType" to MediaType.APPLICATION_OCTET_STREAM_VALUE,
                                "size" to resource.contentLength().toInt()) // Long => Int
                )))
    }

    @Test
    fun testDownloadFile() {

        val resource = ClassPathResource("application.yml")

        mockMvc.perform {
            get("/file/download").run {
                buildRequest(it)
            }
        }
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(content().string(loadFromFile(resource)))
    }
}
