package com.example.aichathelp.data.remote.dto.perplexity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatChoiceDto(
    val index: Int,
    val message: ChatMessageDto,
    @SerialName("finish_reason")
    val finishReason: String?,
)