package com.example.aichathelp.ui.screen.chat.model

import com.example.aichathelp.domain.model.ModelVendor
import com.example.aichathelp.domain.model.PromptType

sealed class ChatIntent {
    data class InputChanged(val text: String) : ChatIntent()
    object SendClicked : ChatIntent()
    object ErrorShown : ChatIntent()
    object RetryClicked : ChatIntent()
    data class PromptTypeChanged(val promptType: PromptType) : ChatIntent()
    object ClearChat : ChatIntent()
    data class TemperatureChanged(val value: Double) : ChatIntent()
    data class TopPChanged(val value: Double) : ChatIntent()
    object ResetConfigClicked : ChatIntent()
    data class ProviderChanged(val provider: ModelVendor) : ChatIntent()
    data class UseHistoryChanged(val value: Boolean) : ChatIntent()
    data class MaxTokensChanged(val value: Int) : ChatIntent()
}