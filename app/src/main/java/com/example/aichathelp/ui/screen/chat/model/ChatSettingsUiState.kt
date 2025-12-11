package com.example.aichathelp.ui.screen.chat.model

import com.example.aichathelp.domain.model.ModelVendor
import com.example.aichathelp.domain.model.PromptType

data class ChatSettingsUiState(
    val provider: ModelVendor,
    val availableProviders: List<ModelVendor>,
    val currentPromptType: PromptType,
    val temperature: Double,
    val topP: Double,
    val maxTokens: Int,
    val useHistory: Boolean = true,
)
