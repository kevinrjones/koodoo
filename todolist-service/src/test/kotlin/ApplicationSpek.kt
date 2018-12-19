package com.knowledgespike

import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.*
import org.amshove.kluent.`should be`
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldNotBeNull
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

/**
 * This example uses the code from the ktor guid and creates a function that itself
 * creates and uses the test engine (see @see[GetTodoSpec]) for an example where the
 * engine is created explicitly
 */
object AltGetTodosSpec : Spek({
    describe("Alt Get the todos") {
        testApp {
            /**
             * Seems to be necessary here to start hen ApplicationTestEngine
             * The problem then is that Intellij Idea does not show the 'it' tests
             * as individual runs
             *
             */
            on("/todos") {
                /**
                 * This is using @see[apply] to run the asserts after the test has executed
                 */
                it("should be OK") {
                    handleRequest(HttpMethod.Get, "/todos").apply {
                        response.status().`should be`(HttpStatusCode.OK)
                    }
                }
            }
        }
    }
})

/**
 * This example creates the TestApplicationEngine engine directly and then uses that engine
 */
object GetTodosSpec : Spek({
    describe("Get the todos") {
        val engine = TestApplicationEngine(createTestEnvironment())
        engine.start(wait = false) // for now we can't eliminate it
        engine.application.moduleWithDependencies(false) // our main module function

        with(engine) {
            it("should be OK to get the list of todos") {
                handleRequest(HttpMethod.Get, "/todos").apply {
                    response.status().`should be`(HttpStatusCode.OK)
                }
            }
            /**
             * These three tests are equivalent ways of doing the same thing
             * They @see[apply],  @see[let] and @see[with] to run the test case
             *
             * Not sure which is idiomatic
             */
            it("should get the todos") {

                handleRequest(HttpMethod.Get, "/todos").apply {
                    response.content
                            .shouldNotBeNull()
                            .shouldContain("database")
                }

                handleRequest(HttpMethod.Get, "/todos").let {
                    it.response.content
                            .shouldNotBeNull()
                            .shouldContain("database")
                }

                with(handleRequest(HttpMethod.Get, "/todos")) {
                    response.content
                            .shouldNotBeNull()
                            .shouldContain("database")
                }
            }

            it("should get the todos") {
                with(handleRequest(HttpMethod.Get, "/todos")) {
                    response.content
                            .shouldNotBeNull()
                            .shouldContain("database")
                }
            }

            it("should create the todos") {
                with(handleRequest(HttpMethod.Post, "/todos")) {
                    response.status().`should be`(HttpStatusCode.Created)
                }
            }

            it("should update the todos") {
                with(handleRequest(HttpMethod.Put, "/todos/1")) {
                    response.status().`should be`(HttpStatusCode.NoContent)
                }
            }

            it("should delete the todos") {
                with(handleRequest(HttpMethod.Delete, "/todos/1")) {
                    response.status().`should be`(HttpStatusCode.NoContent)
                }
            }
        }
    }
})

fun testApp(callback: TestApplicationEngine.() -> Unit) {
    withTestApplication({ moduleWithDependencies(true) }) { callback() }
}

