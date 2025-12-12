package com.example.aichathelp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_summary")
data class ChatSummaryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val createdAt: Long,
    val messagesCount: Int,
    val summaryText: String,
)