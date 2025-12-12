package com.example.aichathelp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.aichathelp.data.local.entity.ChatSummaryEntity

@Dao
interface ChatSummaryDao {

    @Query("SELECT * FROM chat_summary ORDER BY createdAt ASC")
    suspend fun getAllSummaries(): List<ChatSummaryEntity>

    @Query("SELECT * FROM chat_summary ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLastSummary(): ChatSummaryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSummary(summary: ChatSummaryEntity)

    @Query("DELETE FROM chat_summary")
    suspend fun clearAllSummaries()
}
