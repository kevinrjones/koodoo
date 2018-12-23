package com.knowledgespike.todolist.shared

import com.knowledgespike.todolist.TodoListRepository
import java.time.LocalDate

interface TodoService {
    fun getAll(): List<TodoItem>
    fun getTodo(id: Int): TodoItem
    fun delete(id: Int): Boolean
    fun create(todo: TodoItem): Boolean
    fun update(id: Int, todo: TodoItem): Boolean
}

val todo = TodoItem(
    "Add database processing",
    "Add backend support to this code",
    "Kevin",
    LocalDate.of(2018, 12, 18),
    Importance.HIGH
)

val todos = listOf(todo, todo)

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
        return todos[id];
    }

}