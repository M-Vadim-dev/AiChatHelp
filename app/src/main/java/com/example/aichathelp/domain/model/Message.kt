package com.example.aichathelp.domain.model

sealed class MessageType {
    data object Answer : MessageType()
    data object Question : MessageType()
    data class Error(val message: String) : MessageType()
}

data class Message(
    val text: String? = null,
    val time: String,
    val isUser: Boolean,
    val type: MessageType,
    val isError: Boolean = false,
)