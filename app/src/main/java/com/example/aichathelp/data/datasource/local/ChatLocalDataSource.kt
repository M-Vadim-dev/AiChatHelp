package com.example.aichathelp.data.datasource.local

import com.example.aichathelp.data.local.entity.ChatMessageEntity

interface ChatLocalDataSource {
    suspend fun getAllMessages(): List<ChatMessageEntity>
    suspend fun insertMessage(message: ChatMessageEntity)
    suspend fun clearAllMessages()
}
