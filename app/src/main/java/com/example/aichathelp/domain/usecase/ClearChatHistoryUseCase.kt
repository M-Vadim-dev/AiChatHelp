package com.example.aichathelp.domain.usecase

import com.example.aichathelp.domain.repository.ChatRepository
import javax.inject.Inject

class ClearChatHistoryUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke() {
        chatRepository.clearChatHistory()
    }
}
