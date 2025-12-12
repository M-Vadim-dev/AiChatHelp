package com.example.aichathelp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.aichathelp.data.local.dao.ChatMessageDao
import com.example.aichathelp.data.local.dao.ChatSummaryDao
import com.example.aichathelp.data.local.entity.ChatMessageEntity
import com.example.aichathelp.data.local.entity.ChatSummaryEntity

@Database(entities = [ChatMessageEntity::class, ChatSummaryEntity::class], version = 2, exportSchema = false)
abstract class ChatDatabase : RoomDatabase() {
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun chatSummaryDao(): ChatSummaryDao
}