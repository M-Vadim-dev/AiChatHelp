package com.example.aichathelp.ui.mapper

import com.example.aichathelp.domain.model.ChatMessage
import com.example.aichathelp.domain.model.ChatRole
import com.example.aichathelp.ui.screen.chat.model.MessageUi
import com.example.aichathelp.ui.util.toUiTime
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject

class ChatMessageUiMapper @Inject constructor() {

    fun toUi(domain: ChatMessage): MessageUi {
        val localDateTime = Instant.ofEpochMilli(domain.timestamp).atZone(ZoneId.systemDefault()).toLocalDateTime()
        return MessageUi(
            id = domain.id.toString(),
            text = domain.text,
            time = localDateTime.toUiTime(),
            isUser = domain.role == ChatRole.USER
        )
    }

    fun toUi(domainList: List<ChatMessage>): List<MessageUi> {
        return domainList.map { toUi(it) }
    }
}
