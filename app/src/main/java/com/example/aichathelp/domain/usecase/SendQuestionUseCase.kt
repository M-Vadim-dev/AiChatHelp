package com.example.aichathelp.domain.usecase

import com.example.aichathelp.data.remote.dto.ChatMessageDto
import com.example.aichathelp.data.remote.dto.ChatRequestDto
import com.example.aichathelp.domain.model.ChatContext
import com.example.aichathelp.domain.model.ChatRole
import com.example.aichathelp.domain.model.ChatStep
import com.example.aichathelp.domain.repository.ChatRepository
import com.example.aichathelp.domain.util.ChatPrompts.PROMPT_JSON_RESPONSE
import kotlinx.serialization.json.Json
import javax.inject.Inject

class SendQuestionUseCase @Inject constructor(
    private val repository: ChatRepository,
    private val json: Json,
) {
    suspend operator fun invoke(
        userMessage: String,
        chatContext: ChatContext,
    ): ChatStep {
        require(userMessage.isNotBlank()) { "User message must not be blank" }

        val userContent = """
          КОНТЕКСТ: ${json.encodeToString(ChatContext.serializer(), chatContext)}
          Пользователь ответил: $userMessage
        """.trimIndent()

        val messages = listOf(
            ChatMessageDto(role = ChatRole.SYSTEM.value, content = PROMPT_JSON_RESPONSE),
            ChatMessageDto(role = ChatRole.USER.value, content = userContent)
        )

        val request = ChatRequestDto(
            model = "sonar",
            messages = messages,
            maxTokens = 300,
            temperature = 0.2,
            topP = 0.2,
            languagePreference = "russian",
            searchMode = "web",
            returnImages = false,
            returnRelatedQuestions = false
        )

        val dtoResponse = repository.sendChatRequest(request)
        val firstChoice = dtoResponse.choices.firstOrNull()
        val rawAnswer = firstChoice?.message?.content ?: "{}"

        return json.decodeFromString(ChatStep.serializer(), cleanRawJson(rawAnswer))
    }

    private fun cleanRawJson(raw: String): String {
        val trimmed = raw.trim()
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()
        val start = trimmed.indexOf('{')
        val end = trimmed.lastIndexOf('}')
        return if (start != -1 && end != -1 && start < end) {
            trimmed.substring(start, end + 1)
        } else {
            "{}"
        }
    }
}