package com.example.aichathelp.domain.usecase

import com.example.aichathelp.domain.model.ChatConfig
import javax.inject.Inject

class GetDefaultChatConfigUseCase @Inject constructor() {
    operator fun invoke(): ChatConfig = ChatConfig.default()
}