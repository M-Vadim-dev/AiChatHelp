package com.example.aichathelp.domain.model

data class Message(
    val text: String,
    val time: String,
    val isUser: Boolean,
)