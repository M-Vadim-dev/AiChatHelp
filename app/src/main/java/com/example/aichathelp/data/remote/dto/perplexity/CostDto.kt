package com.example.aichathelp.data.remote.dto.perplexity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CostDto(
    @SerialName("input_tokens_cost")
    val inputTokensCost: Double,
    @SerialName("output_tokens_cost")
    val outputTokensCost: Double,
    @SerialName("request_cost")
    val requestCost: Double,
    @SerialName("total_cost")
    val totalCost: Double,
)