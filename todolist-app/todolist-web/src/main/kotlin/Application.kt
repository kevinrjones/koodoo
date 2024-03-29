@file:Suppress("PackageDirectoryMismatch")

package com.knowledgespike.todolist.web

import com.github.mustachejava.DefaultMustacheFactory
import com.knowledgespike.dataaccess.shared.TodoService
import com.knowledgespike.todo.service.TodoServiceImpl
import com.knowledgespike.todolist.IOAuthClient
import com.knowledgespike.todolist.OAuthClient
import io.ktor.application.*
import io.ktor.auth.Authentication
import io.ktor.auth.OAuthServerSettings
import io.ktor.auth.oauth
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.features.StatusPages
import io.ktor.features.origin
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.mustache.Mustache
import io.ktor.request.host
import io.ktor.request.port
import io.ktor.request.uri
import io.ktor.response.respondRedirect
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.server.engine.commandLineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.sessions.*
import org.koin.dsl.module.module
import org.koin.ktor.ext.inject
import org.koin.standalone.StandAloneContext.startKoin

// koin setup
val todoAppAppModule = module {
    single<TodoService> { TodoServiceImpl(get()) }
    single<IOAuthClient> { OAuthClient() }
}

val logonProvider = OAuthServerSettings.OAuth2ServerSettings(
    name = "idsvr",
    authorizeUrl = "https://localhost:5443/connect/authorize",
    accessTokenUrl = "https://localhost:5443/connect/token",
    requestMethod = HttpMethod.Post,
    clientId = "todolistClient",
    clientSecret = "superSecretPassword",
    defaultScopes = listOf("todolistAPI.read", "todolistAPI.write")
)

fun main(args: Array<String>) {
    startKoin(listOf(todoAppAppModule))
    embeddedServer(Netty, commandLineEnvironment(args)).start()
}


@Suppress("unused") // Referenced in application.conf
fun Application.module() {

    val oauthHttpClient: HttpClient = HttpClient(Apache).apply {
        environment.monitor.subscribe(ApplicationStopping) {
            close()
        }
    }

    val todoService: TodoService by inject()

    moduleWithDependencies(todoService, oauthHttpClient)
}

// todo: move all to config
const val oauthAuthentication = "oauthAuthentication"

fun Application.moduleWithDependencies(todoService: TodoService, oauthHttpClient: HttpClient) {


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
            return@status call.respondRedirect("http://localhost:9080", permanent = false)

        }
    }

    install(Mustache) {
        mustacheFactory = DefaultMustacheFactory("templates")
    }

    install(Sessions) {
        cookie<UserSession>("KTOR_SESSION", storage = SessionStorageMemory())
    }

    install(Authentication) {
        oauth(oauthAuthentication) {
            skipWhen { call -> call.sessions.get<UserSession>() != null }
            client = oauthHttpClient
            providerLookup = {
                logonProvider
            }
            urlProvider = { redirectUrl("/todos") }
        }
    }

    install(Routing) {
        val routing = this
        if (isDev) trace {
            application.log.trace(it.buildText())
        }

        todos(todoService)
        staticResources()
    }

}

data class UserSession(val name: String)


val Application.envKind get() = environment.config.property("ktor.environment").getString()
val Application.isDev get() = envKind == "dev"
val Application.isTest get() = envKind == "test"
val Application.isProd get() = envKind != "dev" && envKind != "test"

private fun ApplicationCall.redirectUrl(path: String): String {
    val defaultPort = if (request.origin.scheme == "http") 80 else 443
    val hostPort = request.host()!! + request.port().let { port -> if (port == defaultPort) "" else ":$port" }
    val protocol = request.origin.scheme
    return "$protocol://$hostPort$path"
}
