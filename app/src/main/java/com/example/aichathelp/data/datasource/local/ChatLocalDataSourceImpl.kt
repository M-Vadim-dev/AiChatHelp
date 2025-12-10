package com.example.aichathelp.data.datasource.local

import com.example.aichathelp.data.local.dao.ChatMessageDao
import com.example.aichathelp.data.local.entity.ChatMessageEntity
import javax.inject.Inject

class ChatLocalDataSourceImpl @Inject constructor(
    private val chatMessageDao: ChatMessageDao
) : ChatLocalDataSource {

    override suspend fun getAllMessages(): List<ChatMessageEntity> {
        return chatMessageDao.getAllMessages()
    }

    override suspend fun insertMessage(message: ChatMessageEntity) {
        chatMessageDao.insertMessage(message)
    }

    override suspend fun clearAllMessages() {
        chatMessageDao.clearAllMessages()
    }
}
