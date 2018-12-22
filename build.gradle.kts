import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val logback_version: String by project
val kotlin_version: String by project
val spek_version: String by project
val jackson_version: String by project
val kluent_version: String by project

buildscript {
    repositories {
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.11")
    }
}

plugins {
    java
}

allprojects {
    group = "com.knowledgespike"
    version = "0.0.1"

    apply (plugin="org.jetbrains.kotlin.jvm")

    repositories {
        jcenter()
        mavenLocal()
        maven { url = uri("https://kotlin.bintray.com/ktor") }
    }

    dependencies {
        compile("ch.qos.logback:logback-classic:$logback_version")
        compile("com.fasterxml.jackson.module:jackson-module-kotlin:$jackson_version")
        compile("com.fasterxml.jackson.datatype:jackson-datatype-jsr310$jackson_version")
        compile("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version")

        testCompile("org.amshove.kluent:kluent:$kluent_version")
        testCompile("org.spekframework.spek2:spek-dsl-jvm:$spek_version")

        testRuntime("org.spekframework.spek2:spek-runner-junit5:$spek_version")
    }

    tasks.withType<Test> {
        useJUnitPlatform {
            includeEngines ("spek2")
        }
    }
}

subprojects {

    version = "1.0"
}

project(":todolist-restapi") {

    dependencies {
        "implementation"(project(":todolist-service"))
    }
}

project(":todolist-service") {
}

