import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val arrow_version: String by project
val klaxon_version: String by project

dependencies {
    implementation("io.arrow-kt:arrow-core:$arrow_version")
    implementation("com.beust:klaxon:$klaxon_version")

}

