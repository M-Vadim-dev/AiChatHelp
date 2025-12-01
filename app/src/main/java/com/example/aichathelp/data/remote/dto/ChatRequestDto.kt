package com.example.aichathelp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatRequestDto(
    val model: String,
    val messages: List<ChatMessageDto>,
    @SerialName("max_tokens")
    val maxTokens: Int? = 20,
    val temperature: Double? = 0.2,
    @SerialName("search_mode")
    val searchMode: String? = "web",
    @SerialName("return_images")
    val returnImages: Boolean? = false,
    @SerialName("return_related_questions")
    val returnRelatedQuestions: Boolean? = false
)