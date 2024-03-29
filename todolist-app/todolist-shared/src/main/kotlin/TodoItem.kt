package com.knowledgespike.todolist.shared

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import java.time.LocalDate

data class TodoItem(
    val title: String,
    val details: String,
    val assignedTo: String,
    @JsonSerialize(using = ToStringSerializer::class)
        @JsonDeserialize(using = LocalDateDeserializer::class)
        val dueDate: LocalDate,
    val importance: Importance
)

enum class Importance {
    LOW, MEDIUM, HIGH
}

