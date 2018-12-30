import com.knowledgespike.models.TodoVM
import com.knowledgespike.todolist.shared.TodoService
import com.knowledgespike.todolist.shared.User
import io.ktor.application.call
import io.ktor.mustache.MustacheContent
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get

fun Routing.todosUI(todoService: TodoService) {
    get("/todos") {
        val todos = todoService.getAll()

        val todoVM = TodoVM(todos, User("Kevin Smith"))

        call.respond(
            MustacheContent("todos.hbs", mapOf("todos" to todoVM))
        )
    }
}

