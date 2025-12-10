package com.example.aichathelp.data.remote.dto.perplexity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UsageDto(
    @SerialName("prompt_tokens")
    val promptTokens: Int,
    @SerialName("completion_tokens")
    val completionTokens: Int,
    @SerialName("total_tokens")
    val totalTokens: Int,
    @SerialName("search_context_size")
    val searchContextSize: String,
    val cost: CostDto,
)