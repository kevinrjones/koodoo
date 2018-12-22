package com.knowledgespike.todolist

import com.fasterxml.jackson.databind.SerializationFeature
import com.knowledgespike.todolist.shared.Importance
import com.knowledgespike.todolist.shared.ToDoItem
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.application.log
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.request.uri
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.response.respondText
import io.ktor.routing.*
import java.time.LocalDate


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
fun Application.module() {

    moduleWithDependencies(false)
}

val todo = ToDoItem("Add database processing",
        "Add backend support to this code",
        "Kevin",
        LocalDate.of(2018, 12, 18),
        Importance.HIGH)

fun Application.moduleWithDependencies(testing: Boolean = false) {

    install(StatusPages) {
        // examples - see isDev and isProd code below
        // see application.conf

        // set environment variable KTOR_ENV=prod to run in production
        when {
            isDev -> {
                this.exception<Throwable> { e ->
                    call.respondText(e.localizedMessage, ContentType.Text.Plain, HttpStatusCode.InternalServerError)
                    throw e
                }
            }

            isProd -> {

            }
        }
        
        // just an example - need to add proper unauthorized
        status(HttpStatusCode.Unauthorized) {
            log.info("Unauthorized: ${call.request.uri}")
            return@status call.respondRedirect("/invalid", permanent = false)

        }
    }

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
            disableDefaultTyping()
        }
    }


    routing {
        todoItems()
    }
}


val Application.envKind get() = environment.config.property("ktor.environment").getString()
val Application.isDev get() = envKind == "dev"
val Application.isTest get() = envKind == "test"
val Application.isProd get() = envKind != "dev" && envKind != "test"

fun Routing.todoItems() {

    get("/todos") {
        call.respond(listOf(todo, todo))
    }

    get("/todos/{id}") {
        val id = call.parameters["id"]
        call.respond(todo)
    }

    post("/todos") {
        call.response.status(HttpStatusCode.Created)
    }

    put("/todos/{id}") {
        call.response.status(HttpStatusCode.NoContent)
    }

    delete("/todos/{id}") {
        call.response.status(HttpStatusCode.NoContent)
    }
}


