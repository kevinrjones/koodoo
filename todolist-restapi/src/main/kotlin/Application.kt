package com.knowledgespike.todolist

import com.fasterxml.jackson.databind.SerializationFeature
import com.knowledgespike.todolist.shared.TodoService
import com.knowledgespike.todolist.shared.TodoServiceImpl
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
import io.ktor.response.respondRedirect
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.server.engine.commandLineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.koin.dsl.module.module
import org.koin.ktor.ext.inject
import org.koin.standalone.StandAloneContext.startKoin
import todoItems

// koin setup
val todoAppAppModule = module {
    single<TodoService> { TodoServiceImpl(get()) }
    single<TodoListRepository> { TodoListRepositorySql() }
}


fun main(args: Array<String>): Unit {
    startKoin(listOf(todoAppAppModule))
    embeddedServer(Netty, commandLineEnvironment(args)).start()
}

@Suppress("unused") // Referenced in application.conf
fun Application.module() {

    val todoService: TodoService by inject()
    moduleWithDependencies(todoService)
}

fun Application.moduleWithDependencies(todoService: TodoService) {

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

            isTest -> {
                this.exception<Throwable> { e ->
                    call.response.status(HttpStatusCode.InternalServerError)
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

// old code
// fun Application.routing(configure: Routing.() -> Unit) = install(Routing, configure)
// install (Routing) is the preferred mechanism
//    routing {
//        todoItems(todoService)
//    }

    install(Routing) {
        if (isDev) trace {
            application.log.trace(it.buildText())
        }

        todoItems(todoService)
    }

}


val Application.envKind get() = environment.config.property("ktor.environment").getString()
val Application.isDev get() = envKind == "dev"
val Application.isTest get() = envKind == "test"
val Application.isProd get() = envKind != "dev" && envKind != "test"
