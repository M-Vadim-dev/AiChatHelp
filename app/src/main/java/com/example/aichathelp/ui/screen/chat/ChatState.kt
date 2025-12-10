package com.example.aichathelp.ui.screen.chat

import com.example.aichathelp.domain.model.ChatConfig
import com.example.aichathelp.domain.model.ModelVendor
import com.example.aichathelp.domain.model.PromptType
import com.example.aichathelp.ui.screen.chat.model.MessageUi

data class ChatState(
    val messages: List<MessageUi> = emptyList(),
    val input: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val provider: ModelVendor = ModelVendor.PERPLEXITY,
    val currentPromptType: PromptType = PromptType.PROFESSIONAL,
    val temperature: Double = ChatConfig.creative().temperature,
    val topP: Double = ChatConfig.creative().topP,
    val useHistory: Boolean = true,
)