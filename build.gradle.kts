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

    apply(plugin = "org.jetbrains.kotlin.jvm")

    repositories {
        jcenter()
        mavenLocal()
        maven { url = uri("https://kotlin.bintray.com/ktor") }
    }

    dependencies {
        implementation("ch.qos.logback:logback-classic:$logback_version")
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jackson_version")
        implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jackson_version")
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version")

        testImplementation("org.amshove.kluent:kluent:$kluent_version")
        testImplementation("org.spekframework.spek2:spek-dsl-jvm:$spek_version")

        testRuntime("org.spekframework.spek2:spek-runner-junit5:$spek_version")
    }

    tasks.withType<Test> {
        useJUnitPlatform {
            includeEngines("spek2")
        }
    }
}

subprojects {

    version = "1.0"
}

project(":todolist-shared") {
}

project(":oauth-client") {
}

project(":repository") {
    dependencies {
        implementation(project(":todolist-shared"))
    }

}

project(":dataaccess-service") {
    dependencies {
        implementation(project(":todolist-shared"))
        implementation(project(":repository"))
    }
}

project(":todolist-service") {
    dependencies {
        implementation(project(":todolist-shared"))
        implementation(project(":oauth-client"))
    }
}

project(":todolist-restapi") {

    dependencies {
        implementation(project(":dataaccess-service"))
        implementation(project(":todolist-shared"))
        implementation(project(":repository"))
    }
}

project(":todolist-web") {

    dependencies {
        implementation(project(":todolist-restapi"))
        implementation(project(":todolist-shared"))
        implementation(project(":todolist-service"))
        implementation(project(":oauth-client"))
    }
}

