package com.example.aichathelp.data.mapper

import com.example.aichathelp.data.remote.dto.ChatMessageDto
import com.example.aichathelp.data.remote.dto.ChatRequestDto
import com.example.aichathelp.domain.model.ChatConfig
import com.example.aichathelp.domain.model.ChatRole
import javax.inject.Inject

class ChatRequestMapper @Inject constructor(
) {
    fun toDto(
        userMessage: String,
        config: ChatConfig,
        systemPrompt: String,
    ): ChatRequestDto {
        return ChatRequestDto(
            model = config.model,
            messages = listOf(
                ChatMessageDto(role = ChatRole.SYSTEM.value, content = systemPrompt),
                ChatMessageDto(role = ChatRole.USER.value, content = userMessage)
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
