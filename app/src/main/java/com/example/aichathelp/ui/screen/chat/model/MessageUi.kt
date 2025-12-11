package com.example.aichathelp.ui.screen.chat.model

import androidx.compose.runtime.Immutable

@Immutable
data class MessageUi(
    val id: String,
    val text: String,
    val time: String,
    val isUser: Boolean,
    val error: String? = null,
    val hasAnimated: Boolean = false,
    val isSending: Boolean = false,
    val promptTokens: Int = 0,
    val completionTokens: Int = 0,
    val totalTokens: Int = 0,
    val inputTokensCost: Double = 0.0,
    val outputTokensCost: Double = 0.0,
    val requestCost: Double = 0.0,
    val totalCost: Double = 0.0,
    val isTokenLimitExceeded: Boolean = false,
)
