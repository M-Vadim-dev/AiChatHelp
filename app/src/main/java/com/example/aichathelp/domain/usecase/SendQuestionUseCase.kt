package com.example.aichathelp.domain.usecase

import com.example.aichathelp.data.mapper.ChatRequestMapper
import com.example.aichathelp.data.mapper.JsonResponseMapper
import com.example.aichathelp.domain.model.ChatConfig
import com.example.aichathelp.domain.model.ChatContext
import com.example.aichathelp.domain.model.ChatStep
import com.example.aichathelp.domain.repository.ChatRepository
import kotlinx.serialization.json.Json
import javax.inject.Inject

class SendQuestionUseCase @Inject constructor(
    private val repository: ChatRepository,
    private val requestMapper: ChatRequestMapper,
    private val responseMapper: JsonResponseMapper,
    private val json: Json
) {
    suspend operator fun invoke(
        userMessage: String,
        chatContext: ChatContext,
        config: ChatConfig,
        systemPrompt: String,
    ): Result<ChatStep> {
        require(userMessage.isNotBlank()) { "User message must not be blank" }

        val request = requestMapper.toDto(userMessage, chatContext, config, systemPrompt)

        return try {
            val response = repository.sendChatRequest(request)
            val rawAnswer = response.choices.firstOrNull()?.message?.content ?: "{}"
            val cleaned = responseMapper.cleanRawResponse(rawAnswer)
            val chatStep = json.decodeFromString(ChatStep.serializer(), cleaned)
            Result.success(chatStep)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}