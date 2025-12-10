package com.example.aichathelp.domain.usecase

import com.example.aichathelp.domain.model.ChatMessage
import com.example.aichathelp.domain.repository.ChatRepository
import javax.inject.Inject

class GetChatHistoryUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(): List<ChatMessage> {
        return chatRepository.getChatHistory()
    }
}
