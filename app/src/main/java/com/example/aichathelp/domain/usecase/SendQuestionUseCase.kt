package com.example.aichathelp.domain.usecase

import com.example.aichathelp.data.remote.dto.ChatMessageDto
import com.example.aichathelp.data.remote.dto.ChatRequestDto
import com.example.aichathelp.domain.model.ChatRole
import com.example.aichathelp.domain.model.Message
import com.example.aichathelp.domain.repository.ChatRepository
import com.example.aichathelp.domain.util.ChatPrompts.SHORT_ANSWER
import com.example.aichathelp.ui.util.toUiTime
import java.time.LocalDateTime
import javax.inject.Inject

class SendQuestionUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(question: String): Message {
        require(question.isNotBlank()) { "Question must not be blank" }

        val messages = listOf(
            ChatMessageDto(role = ChatRole.SYSTEM.value, content = SHORT_ANSWER),
            ChatMessageDto(role = ChatRole.USER.value, content = question),
        )

        val request = ChatRequestDto(
            model = "sonar",
            messages = messages,
            maxTokens = 200,
            temperature = 0.2,
            topP = 0.9,
            languagePreference = "russian",
            searchMode = "web",
            returnImages = false,
            returnRelatedQuestions = false
        )

        val dtoResponse = repository.sendChatRequest(request)
        val answer = dtoResponse.choices.firstOrNull()?.message?.content ?: "Empty response"

        return Message(
            text = answer,
            time = LocalDateTime.now().toUiTime(),
            isUser = false
        )
    }
}