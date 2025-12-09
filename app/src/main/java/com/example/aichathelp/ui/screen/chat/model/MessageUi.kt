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
) {
    companion object {
        fun fromDomain(
            message: Message,
            isUser: Boolean = false,
            isSending: Boolean = false,
        ): MessageUi = MessageUi(
            text = message.text,
            time = message.time,
            type = message.type,
            isUser = isUser,
            isSending = isSending,
        )
    }
}