package com.example.aichathelp.data.repository

import android.util.Log
import com.example.aichathelp.data.mapper.ChatRequestMapper
import com.example.aichathelp.data.mapper.JsonResponseMapper
import com.example.aichathelp.data.remote.ChatApi
import com.example.aichathelp.domain.model.ChatConfig
import com.example.aichathelp.domain.model.ChatContext
import com.example.aichathelp.domain.model.ChatStep
import com.example.aichathelp.domain.repository.ChatRepository
import kotlinx.serialization.json.Json
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val api: ChatApi,
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

        val requestDto = requestMapper.toDto(userMessage, chatContext, config, systemPrompt)

        val responseDto = api.chatCompletion(requestDto)

        val rawJson = responseDto.choices.firstOrNull()?.message?.content ?: "{}"

        val cleaned = responseMapper.cleanRawResponse(rawJson)
        Log.d("!!!", cleaned)

        return json.decodeFromString(ChatStep.serializer(), cleaned)
    }

}
