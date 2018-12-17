package com.knowledgespike

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import org.amshove.kluent.*


import org.junit.Test

class ApplicationTest {

    @Test
    fun testRequest() {
        testApp {
            with(handleRequest(HttpMethod.Get, "/todos")) {
                response.status().`should be`(HttpStatusCode.OK)
            }

            with(handleRequest(HttpMethod.Get, "/todos")) {
                response.content
                        .shouldNotBeNull()
                        .shouldContain("Title")
            }

            with(handleRequest(HttpMethod.Get, "/todos")) {
                val mapper = jacksonObjectMapper()
                val item = mapper.readValue<ToDoItem>(response.content!!)
                item.title.shouldBeEqualTo("Title")
            }


        }
    }

    private fun testApp(callback: TestApplicationEngine.() -> Unit) {
        withTestApplication({moduleWithDependencies(true)}) {callback()}
    }
}