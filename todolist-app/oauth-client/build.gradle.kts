import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlin_version: String by project
val fuel_version: String by project
val klaxon_version: String by project
val arrow_version: String by project


group = "com.knowledgespike"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.github.kittinunf.fuel:fuel:$fuel_version")
    implementation("com.beust:klaxon:$klaxon_version")
    implementation("io.arrow-kt:arrow-core:$arrow_version")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}