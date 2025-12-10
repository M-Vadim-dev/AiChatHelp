package com.example.aichathelp.data.datasource.remote

import com.example.aichathelp.data.remote.dto.ChatRequestDto
import com.example.aichathelp.data.remote.dto.ChatResponseDto
import com.example.aichathelp.domain.model.ModelVendor

interface ChatRemoteDataSource {
    suspend fun getChatCompletion(vendor: ModelVendor, request: ChatRequestDto): ChatResponseDto
}
