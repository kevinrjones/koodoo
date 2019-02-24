package com.knowledgespike.todolist

import com.knowledgespike.dataaccess.shared.TodoService
import com.knowledgespike.todolist.restapi.moduleWithDependencies
import com.knowledgespike.todolist.shared.Importance
import com.knowledgespike.todolist.shared.TodoItem
import io.ktor.config.MapApplicationConfig
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.*
import io.mockk.every
import io.mockk.mockk
import org.amshove.kluent.`should be`
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.time.LocalDate

/**
 * This example uses the code from the ktor guide and creates a function that itself
 * creates and uses the test engine (see @see[GetTodoSpec]) for an example where the
 * engine is created explicitly
 */
object AltGetTodosSpec : Spek({

    val todo = TodoItem(
        "Add database processing",
        "Add backend support to this code",
        "Kevin",
        LocalDate.of(2018, 12, 18),
        Importance.HIGH
    )


    describe("Alt Get the todos") {
        testApp {
            /**
             * Seems to be necessary here to start the ApplicationTestEngine
             * The problem then is that Intellij Idea does not show the 'it' tests
             * as individual runs
             *
             */
            on("/todos") {
                /**
                 * This is using @see[apply] to run the asserts after the test has executed
                 */
                it("should be OK") {

                    every { gmockTodoService.getAll() } returns listOf(
                        todo,
                        todo
                    )

                    handleRequest(HttpMethod.Get, "/api/todos").apply {
                        response.status().`should be`(HttpStatusCode.OK)
                    }
                }
            }
        }
    }
})

val gmockTodoService = mockk<TodoService>()

fun testApp(callback: TestApplicationEngine.() -> Unit) {

    withTestApplication({
        with(this) {
            (environment.config as MapApplicationConfig).apply {
                // Set here the properties
                put("ktor.environment", "test")
                put("jwt.jwkIssuer", "http://localhost:5000")
                put("jwt.jwksUrl", "http://localhost:5000")
                put("jwt.jwkRealm", "test")
            }
        }

        moduleWithDependencies(gmockTodoService)
    }) { callback() }
}

