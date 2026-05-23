package com.litert.coach.domain.model

data class ChatMessage(
    val id: Int, val role: String, val content: String,
    val isSummarized: Boolean = false, val createdAt: Long
)

enum class ChatRole { USER, ASSISTANT }
