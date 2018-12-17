allprojects {
    repositories {
        jcenter()
        mavenLocal()
        maven { url = uri("https://kotlin.bintray.com/ktor") }
    }
}

subprojects {
    version = "1.0"
}

