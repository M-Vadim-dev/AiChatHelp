package com.example.aichathelp.domain.repository

import com.example.aichathelp.domain.model.ChatConfig
import com.example.aichathelp.domain.model.ChatContext
import com.example.aichathelp.domain.model.ChatStep

interface ChatRepository {
    suspend fun sendMessage(
        userMessage: String,
        chatContext: ChatContext,
        config: ChatConfig,
        systemPrompt: String,
    ): ChatStep

}
