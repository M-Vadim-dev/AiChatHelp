package com.example.aichathelp.domain.model

data class ChatMessage(
    val id: Long,
    val text: String,
    val role: ChatRole,
    val timestamp: Long,
)
