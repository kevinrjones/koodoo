
@file:Suppress("PackageDirectoryMismatch")
package com.knowledgespike.todo.service

import arrow.core.Either
import arrow.core.flatMap
import com.beust.klaxon.Parser
import com.knowledgespike.dataaccess.shared.TodoService
import com.knowledgespike.todolist.IOAuthClient
import com.knowledgespike.todolist.shared.TodoItem



class TodoServiceImpl(val oauthClient: IOAuthClient) : TodoService {
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
        // should return the error code here, so client creds returns unauthorized and callApi returns 404
        var res = oauthClient.getClientCredential(
            "http://localhost:5000/connect/token",
            "todolistClient",
            "superSecretPassword",
            listOf("todolistAPI.read", "todolistAPI.write")
        ).flatMap {
            oauthClient.callApi("http://localhost:9081/api/todos", it.tokenType, it.token)
        }.map {
            val parser = Parser.default()
            parser.parse(StringBuilder(it)) as List<TodoItem>
        }

        return when (res) {
            is Either.Left -> {
                println(res.a)
                when (res.a) {
                    // what to do on error
                    401 -> listOf()
                    403 -> listOf()
                    404 -> listOf()
                    else -> listOf()
                }
            }
            is Either.Right -> {
                res.b
            }
        }
    }

    override fun getTodo(id: Int): TodoItem {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}