package com.example.aichathelp.ui.screen.chat

import com.example.aichathelp.domain.model.Message

data class ChatState(
    val messages: List<Message> = emptyList(),
    val input: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class ChatIntent {
    data class InputChanged(val text: String) : ChatIntent()
    object SendClicked : ChatIntent()
    object ErrorShown : ChatIntent()
    object RetryClicked : ChatIntent()
}