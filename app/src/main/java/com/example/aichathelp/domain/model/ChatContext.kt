package com.example.aichathelp.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ChatContext(
    val state: String = "select_topic",
    val topic: String = "",
    val answers: Map<String, String> = emptyMap()
)