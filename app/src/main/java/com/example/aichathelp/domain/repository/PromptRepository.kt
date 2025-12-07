package com.example.aichathelp.domain.repository

interface PromptRepository {
    fun getFsmPrompt(): String
    fun getCreativePrompt(): String
}