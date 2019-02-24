package com.knowledgespike.todolist.restapi

import com.knowledgespike.dataaccess.shared.TodoService
import com.knowledgespike.todolist.shared.TodoItem
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.auth.authentication
import io.ktor.auth.jwt.JWTPrincipal
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*

fun Routing.todos(todoService: TodoService) {

    authenticate("jwt") {
        route("/api") {
            var principal: JWTPrincipal?
            handle {
                principal = call.authentication.principal<JWTPrincipal>()
            }
            get("todos") {
                principal = call.authentication.principal<JWTPrincipal>()
                val todos = todoService.getAll()
                call.respond(todos)
            }

            get("todos/{id}") {
                val id = call.parameters["id"] ?: throw IllegalArgumentException("Missing id")
                try {
                    val todo = todoService.getTodo(id.toInt())
                    call.respond(todo)
                } catch (e: Throwable) {
                    call.respond(HttpStatusCode.NotFound)
                }
            }

            // todo: error handling for these methods
            post("todos") {
                val todo = call.receive<TodoItem>()
                todoService.create(todo)
                call.respond(HttpStatusCode.Created)
            }

            put("todos/{id}") {
                val id = call.parameters["id"] ?: throw IllegalArgumentException("Missing id")
                val todo = call.receive<TodoItem>()
                todoService.update(id.toInt(), todo)
                call.respond(HttpStatusCode.NoContent)
            }

            delete("todos/{id}") {
                val id = call.parameters["id"] ?: throw IllegalArgumentException("Missing id")
                todoService.delete(id.toInt())
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}

