package com.example.aichathelp.data.repository

import com.example.aichathelp.data.datasource.local.ChatLocalDataSource
import com.example.aichathelp.data.datasource.remote.ChatRemoteDataSource
import com.example.aichathelp.data.local.entity.ChatMessageEntity
import com.example.aichathelp.data.mapper.ChatMessageMapper
import com.example.aichathelp.data.mapper.ChatRequestMapper
import com.example.aichathelp.data.mapper.JsonResponseMapper
import com.example.aichathelp.domain.model.ChatConfig
import com.example.aichathelp.domain.model.ChatMessage
import com.example.aichathelp.domain.model.ChatRole
import com.example.aichathelp.domain.model.ChatStep
import com.example.aichathelp.domain.model.FinishReason
import com.example.aichathelp.domain.repository.ChatRepository
import kotlinx.serialization.json.Json
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val localDataSource: ChatLocalDataSource,
    private val remoteDataSource: ChatRemoteDataSource,
    private val requestMapper: ChatRequestMapper,
    private val responseMapper: JsonResponseMapper,
    private val chatMessageMapper: ChatMessageMapper,
    private val json: Json,
) : ChatRepository {

    override suspend fun sendMessage(
        userMessage: String,
        config: ChatConfig,
        systemPrompt: String
    ): ChatStep {
        val userEntity = ChatMessageEntity(
            text = userMessage,
            role = ChatRole.USER.value,
            timestamp = System.currentTimeMillis()
        )
        localDataSource.insertMessage(userEntity)

        val allMessages = localDataSource.getAllMessages()

        val historyWithoutLast = if (config.useHistory) {
            allMessages
                .dropLast(1)
                .joinToString(separator = "\n") { entity ->
                    "${entity.role.uppercase()}: ${entity.text}"
                }
        } else {
            ""
        }

        val userContent =
            buildUserContent(lastUserMessage = userMessage, history = historyWithoutLast)

        val requestDto = requestMapper.toDto(userContent, config, systemPrompt)

        val responseDto = remoteDataSource.getChatCompletion(config.provider, requestDto)

        val rawJson = responseDto.choices.firstOrNull()?.message?.content ?: "{}"
        val cleanedJson = responseMapper.cleanRawResponse(rawJson)
        val chatStep = json.decodeFromString(ChatStep.serializer(), cleanedJson)

        val rawFinishReason = responseDto.choices.firstOrNull()?.finishReason
        val finishReasonEnum = mapFinishReason(rawFinishReason)

        val assistantEntity = ChatMessageEntity(
            text = chatStep.answer,
            role = ChatRole.ASSISTANT.value,
            timestamp = System.currentTimeMillis(),
            totalTokens = responseDto.usage.totalTokens,
            totalCost = responseDto.usage.cost?.totalCost,
        )
        localDataSource.insertMessage(assistantEntity)

        return chatStep.copy(
            finishReason = finishReasonEnum,
            promptTokens = responseDto.usage.promptTokens,
            completionTokens = responseDto.usage.completionTokens,
            totalTokens = responseDto.usage.totalTokens,
            totalCost = responseDto.usage.cost?.totalCost ?: 0.0,
            inputTokensCost = responseDto.usage.cost?.inputTokensCost ?: 0.0,
            outputTokensCost = responseDto.usage.cost?.outputTokensCost ?: 0.0,
            requestCost = responseDto.usage.cost?.requestCost ?: 0.0,
        )
    }

    override suspend fun getChatHistory(): List<ChatMessage> {
        return localDataSource.getAllMessages().map { chatMessageMapper.toDomain(it) }
    }

    override suspend fun clearChatHistory() {
        localDataSource.clearAllMessages()
    }

    private fun buildUserContent(
        lastUserMessage: String,
        history: String,
    ): String = if (history.isBlank()) {
        lastUserMessage
    } else {
        """
    ИСТОРИЯ ДИАЛОГА (без текущего вопроса):
    $history
    
    ТЕКУЩИЙ ВОПРОС:
    $lastUserMessage
    """.trimIndent()
    }

    private fun mapFinishReason(raw: String?): FinishReason? =
        when (raw?.lowercase()) {
            "stop" -> FinishReason.STOP
            "length" -> FinishReason.LENGTH
            "content_filter" -> FinishReason.CONTENT_FILTER
            "tool_calls" -> FinishReason.TOOL_CALLS
            else -> null
        }

}
