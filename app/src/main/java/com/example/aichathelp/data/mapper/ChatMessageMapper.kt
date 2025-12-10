package com.example.aichathelp.data.mapper

import com.example.aichathelp.data.local.entity.ChatMessageEntity
import com.example.aichathelp.domain.model.ChatMessage
import com.example.aichathelp.domain.model.ChatRole
import javax.inject.Inject

class ChatMessageMapper @Inject constructor() {

    fun toDomain(entity: ChatMessageEntity): ChatMessage {
        return ChatMessage(
            id = entity.id,
            text = entity.text,
            role = ChatRole.fromString(entity.role),
            timestamp = entity.timestamp
        )
    }

    fun toEntity(domain: ChatMessage): ChatMessageEntity {
        return ChatMessageEntity(
            id = domain.id,
            text = domain.text,
            role = domain.role.value,
            timestamp = domain.timestamp
        )
    }
}
