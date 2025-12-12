package com.example.aichathelp.data.datasource.local

import com.example.aichathelp.data.local.entity.ChatMessageEntity
import com.example.aichathelp.data.local.entity.ChatSummaryEntity

interface ChatLocalDataSource {
    suspend fun getAllMessages(): List<ChatMessageEntity>
    suspend fun getLastMessages(limit: Int): List<ChatMessageEntity>
    suspend fun insertMessage(message: ChatMessageEntity)
    suspend fun clearAllMessages()
    suspend fun getMessagesSince(sinceTimestamp: Long): List<ChatMessageEntity>


    suspend fun getAllSummaries(): List<ChatSummaryEntity>
    suspend fun getLastSummary(): ChatSummaryEntity?
    suspend fun insertSummary(summary: ChatSummaryEntity)
    suspend fun clearAllSummaries()
}
