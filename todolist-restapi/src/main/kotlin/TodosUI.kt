import com.knowledgespike.models.TodoVM
import com.knowledgespike.todolist.UserSession
import com.knowledgespike.todolist.oauthAuthentication
import com.knowledgespike.todolist.shared.TodoService
import com.knowledgespike.todolist.shared.User
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.mustache.MustacheContent
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import io.ktor.sessions.set

fun Routing.todosUI(todoService: TodoService) {
    authenticate(oauthAuthentication) {


        get("/todos") {
            when {
                call.sessions.get<UserSession>() == null -> call.sessions.set(UserSession(name = "John"))
            }

            val todos = todoService.getAll()

            val todoVM = TodoVM(todos, User("Kevin Smith"))

            call.respond(
                MustacheContent("todos.hbs", mapOf("todos" to todoVM))
            )
        }
    }
}

