package com.example.aichathelp.domain.model

data class ChatConfig(
    val provider: ModelVendor = ModelVendor.PERPLEXITY,
    val model: String = provider.defaultModel,
    val maxTokens: Int = provider.maxTokens,
    val temperature: Double = 0.2,
    val topP: Double = 0.2,
    val languagePreference: String = "russian",
    val searchMode: String = "web",
    val returnImages: Boolean = false,
    val returnRelatedQuestions: Boolean = false,
    val useHistory: Boolean = true,
) {
    companion object {
        fun default(provider: ModelVendor = ModelVendor.PERPLEXITY) =
            ChatConfig(provider = provider, model = provider.defaultModel, temperature = 0.6, topP = 0.9)

    }
}
