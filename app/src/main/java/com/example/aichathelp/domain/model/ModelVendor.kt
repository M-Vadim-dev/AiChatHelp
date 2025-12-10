package com.example.aichathelp.domain.model

enum class ModelVendor(val displayName: String, val defaultModel: String, val maxTokens: Int) {
    PERPLEXITY("Perplexity", "sonar", 300),
    DEEPSEEK("DeepSeek", "deepseek-chat", 300),
}