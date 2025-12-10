package com.example.aichathelp.data.remote.dto.perplexity

import kotlinx.serialization.Serializable

@Serializable
data class ChatResponseDto(
    val id: String,
    val model: String,
    val choices: List<ChatChoiceDto>,
    val usage: UsageDto,
)