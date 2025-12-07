package com.example.aichathelp.domain.usecase

import com.example.aichathelp.domain.model.ChatConfig
import com.example.aichathelp.domain.model.ChatContext
import com.example.aichathelp.domain.model.ChatStep
import com.example.aichathelp.domain.repository.ChatRepository
import javax.inject.Inject

class SendQuestionUseCase @Inject constructor(
    private val repository: ChatRepository,
) {
    suspend operator fun invoke(
        userMessage: String,
        chatContext: ChatContext,
        config: ChatConfig,
        systemPrompt: String,
    ): Result<ChatStep> = try {

        val step = repository.sendMessage(userMessage, chatContext, config, systemPrompt)

        Result.success(step)

    } catch (e: Exception) {
        Result.failure(e)
    }
}