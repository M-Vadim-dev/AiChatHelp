package com.example.aichathelp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.aichathelp.data.local.dao.ChatMessageDao
import com.example.aichathelp.data.local.entity.ChatMessageEntity

@Database(entities = [ChatMessageEntity::class], version = 1, exportSchema = false)
abstract class ChatDatabase : RoomDatabase() {
    abstract fun chatMessageDao(): ChatMessageDao
}