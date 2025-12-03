package com.example.aichathelp.domain.model

data class ChatConfig(
    val model: String = "sonar",
    val maxTokens: Int = 300,
    val temperature: Double = 0.2,
    val topP: Double = 0.2,
    val languagePreference: String = "russian",
    val searchMode: String = "web",
    val returnImages: Boolean = false,
    val returnRelatedQuestions: Boolean = false,
) {
    companion object {
        fun default() = ChatConfig()
        fun creative() = ChatConfig(temperature = 0.6, topP = 0.9)
    }
}
