package com.knowledgespike.todolist.web

import io.ktor.http.content.default
import io.ktor.http.content.files
import io.ktor.http.content.static
import io.ktor.http.content.staticRootFolder
import io.ktor.routing.Routing
import java.io.File

fun Routing.staticResources() {

    static {
        // When running under IDEA make sure that working directory is set to this sample's project folder
        staticRootFolder = File("wwwroot")

        static("css") {
            files("css")
        }

        static("js") {
            files("js")
        }

        default("index.html")
    }
}