package com.example.aichathelp.domain.repository

import com.example.aichathelp.data.remote.dto.ChatRequestDto
import com.example.aichathelp.data.remote.dto.ChatResponseDto

interface ChatRepository {
    suspend fun sendChatRequest(request: ChatRequestDto): ChatResponseDto
}
