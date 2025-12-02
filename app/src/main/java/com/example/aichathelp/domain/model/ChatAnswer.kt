package com.example.aichathelp.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ChatAnswer(
    val answer: String = "",
    val question: String = "",
)