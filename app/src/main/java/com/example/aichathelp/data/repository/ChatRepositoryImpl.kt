package com.example.aichathelp.data.repository

import com.example.aichathelp.data.remote.ChatApi
import com.example.aichathelp.data.remote.dto.ChatRequestDto
import com.example.aichathelp.data.remote.dto.ChatResponseDto
import com.example.aichathelp.domain.repository.ChatRepository
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val api: ChatApi
) : ChatRepository {
    override suspend fun sendChatRequest(request: ChatRequestDto): ChatResponseDto =
        api.chatCompletion(request)

}
