import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val moshi_version: String by project
val spek_version: String by project

plugins {
    application
    kotlin("jvm") version "1.3.10"
}

group = "first"
version = "0.0.1"

application {
    mainClassName = "io.ktor.server.netty.EngineMain"
}

repositories {
    maven { url = uri("https://kotlin.bintray.com/ktor") }
}

dependencies {
    compile("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.+")
    compile("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version")
    compile("io.ktor:ktor-server-netty:$ktor_version")
    compile("ch.qos.logback:logback-classic:$logback_version")
    compile("io.ktor:ktor-jackson:$ktor_version")
    compile("io.ktor:ktor-server-core:$ktor_version")
    compile("io.ktor:ktor-server-host-common:$ktor_version")
    compile("io.ktor:ktor-auth:$ktor_version")
    compile("io.ktor:ktor-auth-jwt:$ktor_version")

    testCompile("io.ktor:ktor-server-tests:$ktor_version")
    testCompile("org.amshove.kluent:kluent:1.44")
    testCompile("org.spekframework.spek2:spek-dsl-jvm:$spek_version")
    testCompile("io.ktor:ktor-server-test-host:$ktor_version")

    testRuntime("org.spekframework.spek2:spek-runner-junit5:$spek_version")
}

tasks.test {
    useJUnitPlatform {
        includeEngines ("spek2")
    }
}
