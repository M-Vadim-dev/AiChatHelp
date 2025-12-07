package com.example.aichathelp.data.repository

import com.example.aichathelp.data.config.ChatPrompts
import com.example.aichathelp.domain.repository.PromptRepository
import javax.inject.Inject

class PromptRepositoryImpl @Inject constructor(
    private val chatPrompts: ChatPrompts
) : PromptRepository {
    override fun getFsmPrompt() = chatPrompts.FSM_PROMPT

    override fun getCreativePrompt() = chatPrompts.CREATIVE_PROMPT

}