package com.example.aichathelp.ui.screen.chat.model

import com.example.aichathelp.domain.model.Message
import com.example.aichathelp.domain.model.MessageType
import java.util.UUID

data class MessageUi(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val time: String,
    val type: MessageType,
    val isUser: Boolean,
    val hasAnimated: Boolean = false,
    val isSending: Boolean = false,
    val tokensSpent: Int? = null,
    val costSpent: Double? = null,
    val requestDuration: String? = null,
) {
    companion object {
        fun fromDomain(
            message: Message,
            isUser: Boolean = false,
            isSending: Boolean = false,
            tokensSpent: Int? = null,
            costSpent: Double? = null,
            requestDuration: String? = null,
        ): MessageUi = MessageUi(
            text = message.text,
            time = message.time,
            type = message.type,
            isUser = isUser,
            isSending = isSending,
            tokensSpent = tokensSpent,
            costSpent = costSpent,
            requestDuration = requestDuration,
        )

    }
}