package com.example.aichathelp.data.repository

import com.example.aichathelp.data.mapper.ChatRequestMapper
import com.example.aichathelp.data.mapper.JsonResponseMapper
import com.example.aichathelp.data.remote.ChatApi
import com.example.aichathelp.di.DeepSeekApiClient
import com.example.aichathelp.di.PerplexityApiClient
import com.example.aichathelp.domain.model.ChatConfig
import com.example.aichathelp.domain.model.ChatContext
import com.example.aichathelp.domain.model.ChatStep
import com.example.aichathelp.domain.model.ModelVendor
import com.example.aichathelp.domain.repository.ChatRepository
import kotlinx.serialization.json.Json
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    @param:PerplexityApiClient private val perplexityApi: ChatApi,
    @param:DeepSeekApiClient private val deepSeekApi: ChatApi,
    private val requestMapper: ChatRequestMapper,
    private val responseMapper: JsonResponseMapper,
    private val json: Json,
) : ChatRepository {

    override suspend fun sendMessage(
        userMessage: String,
        chatContext: ChatContext,
        config: ChatConfig,
        systemPrompt: String
    ): ChatStep {
        val startTime = System.currentTimeMillis()

        val requestDto = requestMapper.toDto(userMessage, chatContext, config, systemPrompt)

        val api = getApi(config.provider)

        val responseDto = api.chatCompletion(requestDto)

        val durationMs = System.currentTimeMillis() - startTime
        val durationSec = String.format("%.2f", durationMs / 1000.0)

        val rawJson = responseDto.choices.firstOrNull()?.message?.content ?: "{}"

        val cleanedJson = responseMapper.cleanRawResponse(rawJson)

        val chatStep = json.decodeFromString(ChatStep.serializer(), cleanedJson)

        val tokensSpent = responseDto.usage.totalTokens
        val costSpent = responseDto.usage.cost?.totalCost

        return chatStep.copy(tokensSpent = tokensSpent, costSpent = costSpent, requestDuration = durationSec)
    }

    private fun getApi(vendor: ModelVendor): ChatApi = when (vendor) {
        ModelVendor.PERPLEXITY -> perplexityApi
        ModelVendor.DEEPSEEK -> deepSeekApi
    }

}
