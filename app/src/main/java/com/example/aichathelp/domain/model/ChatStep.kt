package com.example.aichathelp.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ChatStep(
    val state: String = "",
    val answer: String = "",
    val question: String = "",
    val tokensSpent: Int? = null,
    val costSpent: Double? = null,
    val requestDuration: String? = null,
)