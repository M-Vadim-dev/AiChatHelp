package com.example.aichathelp.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ChatStep(
    val answer: String = "",
    val question: String = "",
    val finishReason: FinishReason? = null,
    val promptTokens: Int = 0,
    val completionTokens: Int = 0,
    val totalTokens: Int = 0,
    val inputTokensCost: Double = 0.0,
    val outputTokensCost: Double = 0.0,
    val requestCost: Double = 0.0,
    val totalCost: Double = 0.0,
    )