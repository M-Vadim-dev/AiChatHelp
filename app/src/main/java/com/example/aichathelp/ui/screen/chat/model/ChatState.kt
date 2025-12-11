package com.example.aichathelp.ui.screen.chat.model

data class ChatState(
    val messages: List<MessageUi> = emptyList(),
    val input: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val settings: ChatSettingsUiState,
)
