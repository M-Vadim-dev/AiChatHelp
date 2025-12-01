package com.example.aichathelp.data.remote

import com.example.aichathelp.data.remote.dto.ChatRequestDto
import com.example.aichathelp.data.remote.dto.ChatResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface ChatApi {
    @POST("chat/completions")
    suspend fun chatCompletion(
        @Body request: ChatRequestDto
    ): ChatResponseDto
}