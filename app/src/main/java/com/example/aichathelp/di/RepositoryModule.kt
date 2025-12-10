package com.example.aichathelp.di

import com.example.aichathelp.data.datasource.local.ChatLocalDataSource
import com.example.aichathelp.data.datasource.local.ChatLocalDataSourceImpl
import com.example.aichathelp.data.datasource.remote.ChatRemoteDataSource
import com.example.aichathelp.data.datasource.remote.ChatRemoteDataSourceImpl
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

    @Binds
    abstract fun bindChatLocalDataSource(implementation: ChatLocalDataSourceImpl): ChatLocalDataSource

    @Binds
    abstract fun bindChatRemoteDataSource(implementation: ChatRemoteDataSourceImpl): ChatRemoteDataSource
}