package com.example.aichathelp.ui.screen.chat.mapper

import com.example.aichathelp.domain.model.ChatStep
import com.example.aichathelp.domain.model.FinishReason
import com.example.aichathelp.ui.screen.chat.model.MessageUi
import com.example.aichathelp.ui.util.toUiTime
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

class MessageUiMapper @Inject constructor() {

    fun createUserMessage(text: String): MessageUi {
        return MessageUi(
            id = UUID.randomUUID().toString(),
            text = text,
            time = LocalDateTime.now().toUiTime(),
            isUser = true,
            isSending = true,
            hasAnimated = false
        )
    }

    fun createBotMessage(response: ChatStep): MessageUi {
        return MessageUi(
            id = UUID.randomUUID().toString(),
            text = response.answer,
            time = LocalDateTime.now().toUiTime(),
            isUser = false,
            totalCost = response.totalCost,
            promptTokens = response.promptTokens,
            completionTokens = response.completionTokens,
            totalTokens = response.totalTokens,
            inputTokensCost = response.inputTokensCost,
            outputTokensCost = response.outputTokensCost,
            requestCost = response.requestCost,
            isTokenLimitExceeded = response.finishReason == FinishReason.LENGTH
        )
    }

    fun createWelcomeMessage(): MessageUi {
        return MessageUi(
            id = UUID.randomUUID().toString(),
            text = "Привет! Чем могу помочь?",
            time = LocalDateTime.now().toUiTime(),
            isUser = false
        )
    }

    fun createErrorMessage(error: Throwable): MessageUi {
        val errorMessage = error.message ?: "Unknown error"
        return MessageUi(
            id = UUID.randomUUID().toString(),
            text = errorMessage,
            time = LocalDateTime.now().toUiTime(),
            isUser = false,
            error = errorMessage
        )
    }
}