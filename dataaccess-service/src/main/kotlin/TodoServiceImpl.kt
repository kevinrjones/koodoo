package com.knowledgespike.dataaccess.shared

import com.knowledgespike.todolist.TodoListRepository
import com.knowledgespike.todolist.shared.Importance
import com.knowledgespike.todolist.shared.TodoItem
import java.time.LocalDate

val todo1 = TodoItem(
    "Add database processing 1",
    "Add backend support to this code",
    "Kevin",
    LocalDate.of(2018, 12, 18),
    Importance.HIGH
)

val todo2 = TodoItem(
    "Add database processing 2",
    "Add backend support to this code",
    "Kevin",
    LocalDate.of(2018, 12, 18),
    Importance.HIGH
)

val todos = listOf(todo1, todo2)

class TodoServiceImpl(val todoListRepository: TodoListRepository) : TodoService {
    override fun update(id: Int, todo: TodoItem): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun create(todo: TodoItem): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun delete(id: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAll(): List<TodoItem> {
        return todos
    }

    override fun getTodo(id: Int): TodoItem {
        return todos[id]
    }

}