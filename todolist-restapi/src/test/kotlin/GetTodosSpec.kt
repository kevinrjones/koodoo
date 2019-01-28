package com.knowledgespike.todolist

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.knowledgespike.dataaccess.shared.TodoService
import com.knowledgespike.todolist.restapi.moduleWithDependencies
import com.knowledgespike.todolist.shared.Importance
import com.knowledgespike.todolist.shared.TodoItem
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.config.MapApplicationConfig
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.createTestEnvironment
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import org.amshove.kluent.*
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.time.LocalDate


/**
 * This example creates the TestApplicationEngine engine directly and then uses that engine
 */
object GetTodosSpec : Spek({

    val todo = TodoItem(
        "Add database processing",
        "Add backend support to this code",
        "Kevin",
        LocalDate.of(2018, 12, 18),
        Importance.HIGH
    )



    describe("Get the todos") {
        val engine = TestApplicationEngine(createTestEnvironment())
        engine.start(wait = false) // for now we can't eliminate it

        // Initialise testing environment
        // equivalent to 'application.conf'
        with(engine) {
            (environment.config as MapApplicationConfig).apply {
                // Set here the properties
                put("ktor.environment", "test")
                put("jwt.jwkIssuer", "http://localhost:5000")
                put("jwt.jwksUrl", "http://localhost:5000")
                put("jwt.jwkRealm", "test")
            }
        }

        val mockTodoService= mockk<TodoService>()

        beforeEachTest {
            clearMocks(mockTodoService)
        }

        engine.application.moduleWithDependencies(mockTodoService) // our main module function
        val mapper = jacksonObjectMapper()
            .registerModule(JavaTimeModule())

        with(engine) {

            it("should be OK to get the list of todos") {
                every { mockTodoService.getAll() } returns listOf(todo, todo)

                handleRequest(HttpMethod.Get, "/api/todos").apply {
                    response.status().`should be`(HttpStatusCode.OK)
                }
            }
            /**
             * These three tests are equivalent ways of doing the same thing
             * They use @see[apply],  @see[let] and @see[with] to run the test case
             *
             * Not sure which is idiomatic (yet)
             */
            it("should get the todos") {
                every { mockTodoService.getAll() } returns listOf(todo, todo)

                handleRequest(HttpMethod.Get, "/api/todos").apply {
                    response.content
                        .shouldNotBeNull()
                        .shouldContain("database")
                }

                handleRequest(HttpMethod.Get, "/api/todos").let {
                    it.response.content
                        .shouldNotBeNull()
                        .shouldContain("database")
                }

                with(handleRequest(HttpMethod.Get, "/api/todos")) {
                    response.content
                        .shouldNotBeNull()
                        .shouldContain("database")
                }
            }

            it("should get the todos") {
                every { mockTodoService.getAll() } returns listOf(todo, todo)

                with(handleRequest(HttpMethod.Get, "/api/todos")) {
                    response.content
                        .shouldNotBeNull()
                        .shouldContain("database")
                }
            }

            it("should get the corrrect number of todos") {
                every { mockTodoService.getAll() } returns listOf(todo, todo)

                with(handleRequest(HttpMethod.Get, "/api/todos")) {
                    val todos = mapper.readValue<List<TodoItem>>(response.content!!)
                    todos.size.shouldBe(2)
                }
            }

            it("should create the todos") {
                every { mockTodoService.create(any()) } returns true

                with(handleRequest(HttpMethod.Post, "/api/todos"){
                    setBody(mapper.writeValueAsString(todo))
                }) {
                    response.status().`should be`(HttpStatusCode.Created)
                }
            }

            it("should update the todos") {
                every { mockTodoService.update(any(), any()) } returns true
                with(handleRequest(HttpMethod.Put, "/api/todos/1"){
                    setBody(mapper.writeValueAsString(todo))
                }) {
                    response.status().`should be`(HttpStatusCode.NoContent)
                }
            }

            it("should delete the todos") {
                every { mockTodoService.delete(any()) } returns true
                with(handleRequest(HttpMethod.Delete, "/api/todos/1")) {
                    response.status().`should be`(HttpStatusCode.NoContent)
                }
            }

            it("should get the todo if the id is set") {
                every { mockTodoService.getTodo(1) } returns todo

                with(handleRequest(HttpMethod.Get, "/api/todos/1")) {
                    response.content
                        .shouldNotBeNull()
                        .shouldContain("database")
                }
            }

            it("should return an error if the id is invalid") {
                every { mockTodoService.getTodo(1) } throws Exception()

                with(handleRequest(HttpMethod.Get, "/api/todos/1")) {
                    response.status().shouldEqual(HttpStatusCode.NotFound)
                }
            }
        }
    }
})

