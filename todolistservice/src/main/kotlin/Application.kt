package com.knowledgespike

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.http.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
fun Application.module(testing: Boolean = false) {
    routing {
        get("/") {
            call.respondText("Hello, world!", contentType = ContentType.Text.Plain)
        }

    }
}


