package com.knowledgespike.models

import com.knowledgespike.todolist.shared.TodoItem
import com.knowledgespike.todolist.shared.User


data class TodoVM(private val todos: List<TodoItem>, private val user: User) {
    val userName = user.name
    val todoItems = todos
}





