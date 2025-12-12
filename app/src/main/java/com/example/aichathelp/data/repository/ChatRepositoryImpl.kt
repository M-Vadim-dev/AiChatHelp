package com.example.aichathelp.data.repository

import android.util.Log
import com.example.aichathelp.data.config.ChatPrompts.SUMMARIZE_PROMPT
import com.example.aichathelp.data.datasource.local.ChatLocalDataSource
import com.example.aichathelp.data.datasource.remote.ChatRemoteDataSource
import com.example.aichathelp.data.local.entity.ChatMessageEntity
import com.example.aichathelp.data.local.entity.ChatSummaryEntity
import com.example.aichathelp.data.mapper.ChatMessageMapper
import com.example.aichathelp.data.mapper.ChatRequestMapper
import com.example.aichathelp.data.mapper.JsonResponseMapper
import com.example.aichathelp.domain.model.ChatConfig
import com.example.aichathelp.domain.model.ChatMessage
import com.example.aichathelp.domain.model.ChatRole
import com.example.aichathelp.domain.model.ChatStep
import com.example.aichathelp.domain.model.FinishReason
import com.example.aichathelp.domain.model.ModelVendor
import com.example.aichathelp.domain.repository.ChatRepository
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
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

        val fullContent = if (config.useHistory && !config.useSummaryCompression) {
            val allMessages = localDataSource.getAllMessages().dropLast(1)
            val historyWithoutLast = allMessages.joinToString("\n") {
                "${it.role.uppercase()}: ${it.text}"
            }
            buildUserContent(userMessage, historyWithoutLast)
        } else if (config.useSummaryCompression) {
            buildSummaryHistoryContext(userMessage)
        } else {
            userMessage
        }

        val requestDto = requestMapper.toDto(fullContent, config, systemPrompt)
        Log.d(TAG, "=== MAIN REQUEST ===")
        Log.d(TAG, "Content length: ${fullContent.length}")
        Log.d(TAG, "Full content preview: ${fullContent.take(2000)}...")
        Log.d(TAG, "Request DTO: ${json.encodeToString(JsonElement.serializer(), json.parseToJsonElement(json.encodeToString(requestDto)))}")

        val responseDto = remoteDataSource.getChatCompletion(config.provider, requestDto)

        Log.w(TAG, "=== MAIN RESPONSE ===")
        Log.w(TAG, "Raw content: ${responseDto.choices.firstOrNull()?.message?.content?.take(300) ?: "empty"}")
        Log.e(TAG, "Tokens: ${responseDto.usage.promptTokens}/${responseDto.usage.completionTokens}/${responseDto.usage.totalTokens}")

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
        localDataSource.clearAllSummaries()
    }

    private suspend fun buildSummaryHistoryContext(userMessage: String): String {
        val lastSummaryTimestamp = localDataSource.getLastSummary()?.createdAt ?: 0L
        val messagesSinceLastSummary = localDataSource.getMessagesSince(lastSummaryTimestamp)

        Log.i(TAG, "Messages since last summary: ${messagesSinceLastSummary.size}/$SUMMARY_THRESHOLD")

        val summaryText = if (messagesSinceLastSummary.size >= SUMMARY_THRESHOLD) {
            val messagesForSummary = messagesSinceLastSummary.dropLast(1)
            val newSummary = summarizeMessages(messagesForSummary)

            localDataSource.insertSummary(
                ChatSummaryEntity(
                    createdAt = System.currentTimeMillis(),
                    messagesCount = messagesForSummary.size,
                    summaryText = newSummary
                )
            )
            newSummary
        } else {
            localDataSource.getLastSummary()?.summaryText.orEmpty()
        }

        val currentSummaryTimestamp = localDataSource.getLastSummary()?.createdAt ?: 0L
        val tailMessages = localDataSource.getMessagesSince(currentSummaryTimestamp)
            .dropLast(1)
            .takeLast(TAIL_MESSAGES_LIMIT)
            .sortedBy { it.timestamp }

        val summaryPart = if (summaryText.isNotBlank()) "SUMMARY: $summaryText\n" else ""
        val tailPart = tailMessages.joinToString("\n") { "${it.role.uppercase()}: ${it.text}" }

        return "ИСТОРИЯ ДИАЛОГА (без текущего вопроса):\n$summaryPart$tailPart\nТЕКУЩИЙ ВОПРОС: $userMessage"
    }


    private suspend fun summarizeMessages(
        messages: List<ChatMessageEntity>,
    ): String {
        val historyText = messages.joinToString("\n") { e ->
            "${e.role.uppercase()}: ${e.text}"
        }

        val summarizeConfig = ChatConfig(
            provider = ModelVendor.DEEPSEEK,
            temperature = 0.4,
            maxTokens = 200,
            useHistory = false,
        )

        val requestDto = requestMapper.toDto(
            userMessage = historyText,
            config = summarizeConfig,
            systemPrompt = SUMMARIZE_PROMPT
        )

        val responseDto = remoteDataSource.getChatCompletion(summarizeConfig.provider, requestDto)
        val text = responseDto.choices.firstOrNull()?.message?.content.orEmpty()
        return text.trim()
    }

    private fun buildUserContent(
        lastUserMessage: String,
        history: String,
    ): String = if (history.isBlank()) {
        lastUserMessage
    } else {
        "ИСТОРИЯ ДИАЛОГА (без текущего вопроса):\n$history\nТЕКУЩИЙ ВОПРОС: $lastUserMessage"
    }

    private fun mapFinishReason(raw: String?): FinishReason? =
        when (raw?.lowercase()) {
            "stop" -> FinishReason.STOP
            "length" -> FinishReason.LENGTH
            "content_filter" -> FinishReason.CONTENT_FILTER
            "tool_calls" -> FinishReason.TOOL_CALLS
            else -> null
        }

    companion object {
        private const val TAG = "ChatRepositoryImpl"
        private const val SUMMARY_THRESHOLD = 10
        private const val TAIL_MESSAGES_LIMIT = 5
    }
}
