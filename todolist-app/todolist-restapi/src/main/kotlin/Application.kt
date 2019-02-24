@file:Suppress("PackageDirectoryMismatch")

package com.knowledgespike.todolist.restapi

import com.auth0.jwk.JwkProviderBuilder
import com.auth0.jwt.interfaces.Payload
import com.fasterxml.jackson.databind.SerializationFeature
import com.knowledgespike.dataaccess.shared.TodoService
import com.knowledgespike.dataaccess.shared.TodoServiceImpl
import com.knowledgespike.todolist.TodoListRepository
import com.knowledgespike.todolist.TodoListRepositorySql
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.application.log
import io.ktor.auth.Authentication
import io.ktor.auth.jwt.JWTCredential
import io.ktor.auth.jwt.JWTPrincipal
import io.ktor.auth.jwt.jwt
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.request.uri
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.server.engine.commandLineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.koin.dsl.module.module
import org.koin.ktor.ext.inject
import org.koin.standalone.StandAloneContext.startKoin
import java.net.URL
import java.util.concurrent.TimeUnit

// koin setup
val todoAppAppModule = module {
    single<TodoService> { TodoServiceImpl(get()) }
    single<TodoListRepository> { TodoListRepositorySql() }
}

fun main(args: Array<String>) {
    startKoin(listOf(todoAppAppModule))
    embeddedServer(Netty, commandLineEnvironment(args)).start(true)
}


@Suppress("unused") // Referenced in application.conf
fun Application.module() {

    val todoService: TodoService by inject()
    moduleWithDependencies(todoService)
}

// todo: move all to config


fun Application.moduleWithDependencies(todoService: TodoService) {

    // At the moment passing -config=application.dev.conf on the commant line

//    var config = HoconApplicationConfig(ConfigFactory.load("application.conf"))
//    when {
//        isDev -> {
//            config = HoconApplicationConfig(ConfigFactory.load("application.dev.conf"))
//        }
//    }

    val jwkIssuer = environment.config.property("jwt.jwkIssuer").getString() // "http://localhost:5000"
    val jwksUrl =
        URL(environment.config.property("jwt.jwksUrl").getString()) // "http://localhost:5000/.well-known/openid-configuration/jwks"
    val jwkRealm = environment.config.property("jwt.jwkRealm").getString() // "ktor jwt auth test"
    val jwkProvider = JwkProviderBuilder(jwksUrl)
        .cached(10, 24, TimeUnit.HOURS)
        .rateLimited(10, 1, TimeUnit.MINUTES)
        .build()
    val audience = "http://localhost:5000/resources"


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
//            return@status call.respondRedirect("http://localhost:9080", permanent = false)
        }
    }

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
            disableDefaultTyping()
        }
    }

    install(Authentication) {
        jwt("jwt") {
            verifier(jwkProvider, jwkIssuer)
            realm = jwkRealm
            var cred: JWTCredential
            var principal: JWTPrincipal
            validate { credentials ->
                cred = credentials
                log.debug("Credentials audience: ${credentials.payload.audience}")
                log.debug("audience: $audience")

                if(credentials.payload.hasRequiredScope("todolistAPI.read")) {
                    log.debug("has scope: todolistAPI.read")
                }

                if (credentials.payload.audience.contains(audience)) {
                    principal = JWTPrincipal(credentials.payload)
                    principal
                } else
                    null
            }
        }
    }



    install(Routing) {
        if (isDev) trace {
            application.log.trace(it.buildText())
        }

        todos(todoService)
    }

}


val Application.envKind get() = environment.config.property("ktor.environment").getString()
val Application.isDev get() = envKind == "dev"
val Application.isTest get() = envKind == "test"
val Application.isProd get() = envKind != "dev" && envKind != "test"

val Payload.scopes :  List<String>
    get() = this.getClaim("scope")!!.asArray(String::class.java).asList()

fun Payload.hasRequiredScope(scope: String) = this.scopes.contains(scope)
