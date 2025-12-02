package com.example.aichathelp.domain.util

object ChatPrompts {
    private const val JSON_RULES =
        "КРИТИЧЕСКИ ВАЖНО: НЕ добавляй НИКАКИХ ссылок, цитат [1],[2] или источников. " +
                "Ответ ТОЛЬКО чистый валидный JSON-объект БЕЗ markdown, текста вне JSON, БЕЗ скобок []. " +
                "Никаких объяснений, никаких ``` "

    private const val JSON_FORMAT =
        "СТРОГО ФОРМАТ: {\"answer\": \"краткий ответ без цитат\", \"question\": \"краткий вопрос для уточнения\". " +
                "Без полей, markdown, ссылок. Только эти два поля. "

    private const val CHAT_RULES =
        "Ты — мой персональный ассистент помощник, который может поддержать разговор. " +
                "Отвечай БЕЗ цитат и ссылок. Кратко, как в чате, одним предложением. " +
                "Просто ответ и вопрос."
    const val PROMPT_JSON_RESPONSE = JSON_RULES + JSON_FORMAT + CHAT_RULES

}