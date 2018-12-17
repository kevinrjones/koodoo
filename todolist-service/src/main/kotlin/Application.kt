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


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
fun Application.module() {

    moduleWithDependencies(false)
}


fun Application.moduleWithDependencies(testing: Boolean = false) {

    install(StatusPages)
    install(ContentNegotiation) {
        jackson{
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
        call.respond(ToDoItem("Title", "Details", "User", Date(), Importance.HIGH))
    }

    get("/todos/{id}") {
        val id = call.parameters["id"]
        call.respond(ToDoItem("Title", "Details", "User", Date(), Importance.HIGH))
    }

    post("/todos") {
        call.respond(ToDoItem("Title", "Details", "User", Date(), Importance.HIGH))
    }

    put("/todos/{id}") {
        call.respond(ToDoItem("Title", "Details", "User", Date(), Importance.HIGH))
    }
}


data class ToDoItem(val title: String , val details: String, val assignedTo: String, val dueDate: Date, val importance: Importance)

enum class Importance {
    LOW, MEDIUM, HIGH
}