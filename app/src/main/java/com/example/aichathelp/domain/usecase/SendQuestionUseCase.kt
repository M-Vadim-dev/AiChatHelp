package com.example.aichathelp.domain.usecase

import com.example.aichathelp.data.remote.dto.ChatMessageDto
import com.example.aichathelp.data.remote.dto.ChatRequestDto
import com.example.aichathelp.domain.model.Message
import com.example.aichathelp.domain.repository.ChatRepository
import javax.inject.Inject

class SendQuestionUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(question: String): Message {
        require(question.isNotBlank()) { "Question must not be blank" }

        val request = ChatRequestDto(
            model = "sonar-deep-research",
            messages = listOf(ChatMessageDto(role = "user", content = question)),
            maxTokens = 20,
            temperature = 0.2,
            searchMode = "web",
            returnImages = false,
            returnRelatedQuestions = false
        )

        val dtoResponse = repository.sendChatRequest(request)
        val answer = dtoResponse.choices.firstOrNull()?.message?.content ?: "Empty response"

        return Message(text = answer, isUser = false)
    }
}