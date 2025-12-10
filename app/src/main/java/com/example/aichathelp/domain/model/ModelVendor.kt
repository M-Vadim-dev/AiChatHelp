package com.example.aichathelp.domain.model

enum class ModelVendor(val displayName: String, val defaultModel: String) {
    PERPLEXITY("Perplexity", "sonar"),
    DEEPSEEK("DeepSeek", "deepseek-chat"),
}