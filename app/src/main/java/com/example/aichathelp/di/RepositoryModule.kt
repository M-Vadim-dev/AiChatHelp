package com.example.aichathelp.di

import com.example.aichathelp.data.repository.ChatRepositoryImpl
import com.example.aichathelp.domain.repository.ChatRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindChatRepository(implementation: ChatRepositoryImpl): ChatRepository
}