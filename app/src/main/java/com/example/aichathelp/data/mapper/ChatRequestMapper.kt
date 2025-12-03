package com.example.aichathelp.data.mapper

import com.example.aichathelp.data.remote.dto.ChatMessageDto
import com.example.aichathelp.data.remote.dto.ChatRequestDto
import com.example.aichathelp.domain.model.ChatConfig
import com.example.aichathelp.domain.model.ChatContext
import com.example.aichathelp.domain.model.ChatRole
import kotlinx.serialization.json.Json
import javax.inject.Inject

class ChatRequestMapper @Inject constructor(
    private val json: Json,
) {
    fun toDto(
        userMessage: String,
        chatContext: ChatContext,
        config: ChatConfig,
        systemPrompt: String,
    ): ChatRequestDto {
        val userContent = """
            КОНТЕКСТ: ${json.encodeToString(ChatContext.serializer(), chatContext)}
            Пользователь ответил: $userMessage
        """.trimIndent()

        return ChatRequestDto(
            model = config.model,
            messages = listOf(
                ChatMessageDto(role = ChatRole.SYSTEM.value, content = systemPrompt),
                ChatMessageDto(role = ChatRole.USER.value, content = userContent)
            ),
            maxTokens = config.maxTokens,
            temperature = config.temperature,
            topP = config.topP,
            languagePreference = config.languagePreference,
            searchMode = config.searchMode,
            returnImages = config.returnImages,
            returnRelatedQuestions = config.returnRelatedQuestions
        )
    }
}
