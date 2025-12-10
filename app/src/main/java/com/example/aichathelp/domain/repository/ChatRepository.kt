package com.example.aichathelp.domain.repository

import com.example.aichathelp.domain.model.ChatConfig
import com.example.aichathelp.domain.model.ChatMessage
import com.example.aichathelp.domain.model.ChatStep

interface ChatRepository {

    suspend fun sendMessage(
        userMessage: String,
        config: ChatConfig,
        systemPrompt: String
    ): ChatStep

    suspend fun getChatHistory(): List<ChatMessage>

    suspend fun clearChatHistory()
}
