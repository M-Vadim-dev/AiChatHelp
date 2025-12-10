package com.example.aichathelp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val text: String,
    val role: String,
    val timestamp: Long,
    val totalTokens: Int? = null,
    val totalCost: Double? = null,
)