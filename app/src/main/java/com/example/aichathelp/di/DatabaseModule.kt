package com.example.aichathelp.di

import android.content.Context
import androidx.room.Room
import com.example.aichathelp.data.local.ChatDatabase
import com.example.aichathelp.data.local.dao.ChatMessageDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ChatDatabase =
        Room.databaseBuilder(context, ChatDatabase::class.java, "chat_db").build()

    @Provides
    fun provideChatDao(db: ChatDatabase): ChatMessageDao = db.chatMessageDao()
}