package com.example.aichathelp.domain.model

enum class ModelVendor(
    val displayName: String,
    val defaultModel: String,
    val maxTokens: Int,
    val maxAllowedTokens: Int,
    ) {
    PERPLEXITY("Perplexity", "sonar", maxTokens = 300, maxAllowedTokens = 1500),
    DEEPSEEK("DeepSeek", "deepseek-chat", maxTokens = 300, maxAllowedTokens = 1500),
}