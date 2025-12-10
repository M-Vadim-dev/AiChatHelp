package com.example.aichathelp.data.datasource.remote

import com.example.aichathelp.data.remote.ChatApi
import com.example.aichathelp.data.remote.dto.ChatRequestDto
import com.example.aichathelp.data.remote.dto.ChatResponseDto
import com.example.aichathelp.di.DeepSeekApiClient
import com.example.aichathelp.di.PerplexityApiClient
import com.example.aichathelp.domain.model.ModelVendor
import javax.inject.Inject

class ChatRemoteDataSourceImpl @Inject constructor(
    @param:PerplexityApiClient private val perplexityApi: ChatApi,
    @param:DeepSeekApiClient private val deepSeekApi: ChatApi
) : ChatRemoteDataSource {

    override suspend fun getChatCompletion(vendor: ModelVendor, request: ChatRequestDto): ChatResponseDto {
        val api = when (vendor) {
            ModelVendor.PERPLEXITY -> perplexityApi
            ModelVendor.DEEPSEEK -> deepSeekApi
        }
        return api.chatCompletion(request)
    }
}
