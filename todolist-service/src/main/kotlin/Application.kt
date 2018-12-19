package com.knowledgespike

import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.*
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.jackson.jackson
import java.util.*
import com.fasterxml.jackson.databind.util.StdDateFormat
import io.ktor.http.HttpStatusCode


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
fun Application.module() {

    moduleWithDependencies(false)
}

val todo = ToDoItem("Add database processing",
        "Add backend support to this code",
        "Kevin", Date(2018, 12, 18),
        Importance.HIGH)

fun Application.moduleWithDependencies(testing: Boolean = false) {

    install(StatusPages)
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
            dateFormat = StdDateFormat()
            disableDefaultTyping()
        }
    }


    routing {
        todoItems()
    }
}

fun Routing.todoItems() {

    get("/todos") {
        call.respond(todo)
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


data class ToDoItem(val title: String, val details: String, val assignedTo: String, val dueDate: Date, val importance: Importance)

enum class Importance {
    LOW, MEDIUM, HIGH
}