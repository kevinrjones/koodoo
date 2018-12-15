# Build the todo application

The application needs
1. Categories
1. User
1. Todos
1. Dates
1. Assignees


## First steps
1. Kotlin Gradle file (actual project is a multi module Gradle project)
1. Application.kt file with simple Get
1. Use main directly and run the code from within that

After that can then move to a module
1. `Application.module()` adds an extension method  to the application class that is run

## Initial test
1. Change code to have an an `Application.module()` and an `Application.module(testing: Boolean = false)` method
Can call one from the other, this second method makes testing easier
1. Add a test 
``` kotlin
class ApplicationTest {

    @Test
    fun testRequest() {
        testApp {
            with(handleRequest(HttpMethod.Get, "/")) {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("Hello, world!", response.content)
            }
        }
    }

    private fun testApp(callback: TestApplicationEngine.() -> Unit) {
        withTestApplication({moduleWithDependencies(true)}) {callback()}
    }
}
```
