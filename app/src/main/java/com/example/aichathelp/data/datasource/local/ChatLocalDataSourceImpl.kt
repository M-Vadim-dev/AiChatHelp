package com.example.aichathelp.data.datasource.local

import com.example.aichathelp.data.local.dao.ChatMessageDao
import com.example.aichathelp.data.local.dao.ChatSummaryDao
import com.example.aichathelp.data.local.entity.ChatMessageEntity
import com.example.aichathelp.data.local.entity.ChatSummaryEntity
import javax.inject.Inject

class ChatLocalDataSourceImpl @Inject constructor(
    private val chatMessageDao: ChatMessageDao,
    private val chatSummaryDao: ChatSummaryDao,
) : ChatLocalDataSource {

    override suspend fun getAllMessages(): List<ChatMessageEntity> = chatMessageDao.getAllMessages()

    override suspend fun getLastMessages(limit: Int): List<ChatMessageEntity> =
        chatMessageDao.getLastMessages(limit)

    override suspend fun insertMessage(message: ChatMessageEntity) {
        chatMessageDao.insertMessage(message)
    }

    override suspend fun clearAllMessages() {
        chatMessageDao.clearAllMessages()
    }

    override suspend fun getMessagesSince(sinceTimestamp: Long): List<ChatMessageEntity> =
        chatMessageDao.getMessagesSince(sinceTimestamp)

    override suspend fun getAllSummaries(): List<ChatSummaryEntity> =
        chatSummaryDao.getAllSummaries()

    override suspend fun getLastSummary(): ChatSummaryEntity? = chatSummaryDao.getLastSummary()

    override suspend fun insertSummary(summary: ChatSummaryEntity) {
        chatSummaryDao.insertSummary(summary)
    }

    override suspend fun clearAllSummaries() {
        chatSummaryDao.clearAllSummaries()
    }
}
