package com.example.aichathelp.domain.model

enum class ChatRole(val value: String) {
    SYSTEM("system"),
    USER("user"),
    ASSISTANT("assistant");

    companion object {
        fun fromString(value: String): ChatRole {
            return entries.find { it.value.equals(value, ignoreCase = true) } ?: USER
        }
    }
}
