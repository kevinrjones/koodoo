package com.knowledgespike.todolist


import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.knowledgespike.todolist.restapi.moduleWithDependencies
import com.knowledgespike.todolist.shared.TodoItem
import com.knowledgespike.todolist.shared.TodoService
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import io.mockk.mockk
import org.amshove.kluent.`should be`
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldNotBeNull
import org.junit.Test

class ApplicationTest {

    @Test
    fun testGetTodos() {
        testApp {
            // can handle tests this way...
            handleRequest(HttpMethod.Get, "/todos").apply {
                response.status().`should be`(HttpStatusCode.OK)
            }

            // or using with
            with(handleRequest(HttpMethod.Get, "/todos")) {
                response.content
                        .shouldNotBeNull()
                        .shouldContain("database")
            }

            with(handleRequest(HttpMethod.Get, "/todos")) {
                val mapper = jacksonObjectMapper()
                val item = mapper.readValue<TodoItem>(response.content!!)
                item.title.shouldBeEqualTo("Add database processing")
            }
        }
    }

    @Test
    fun testCreatingTodos() {
        testApp {

            with(handleRequest(HttpMethod.Post, "/todos")) {
                response.status().`should be`(HttpStatusCode.Created)
            }
        }
    }

    @Test
    fun testUpdatingTodos() {
        testApp {


            with(handleRequest(HttpMethod.Put, "/todos/1")) {
                response.status().`should be`(HttpStatusCode.NoContent)
            }
        }
    }

    @Test
    fun testDeletingTodos() {
        testApp {

            with(handleRequest(HttpMethod.Delete, "/todos/1")) {
                response.status().`should be`(HttpStatusCode.NoContent)
            }

//            with(handleRequest(HttpMethod.Delete, "/todos/")) {
//                response.status().`should be`(HttpStatusCode.NotFound)
//            }
        }
    }

    private fun testApp(callback: TestApplicationEngine.() -> Unit) {
        val mockTodoService = mockk<TodoService>()
        // todo: remove this
        val oauthHttpClient: HttpClient = HttpClient(Apache)
        withTestApplication({ moduleWithDependencies(mockTodoService, oauthHttpClient) }) { callback() }
    }
}