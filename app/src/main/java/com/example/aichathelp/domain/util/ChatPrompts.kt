package com.example.aichathelp.domain.util

object ChatPrompts {
    private const val JSON_RULES =
        "КРИТИЧЕСКИ ВАЖНО: НЕ добавляй никаких ссылок, цитат или источников. " +
                "Ответ ТОЛЬКО в виде чистого валидного JSON-объекта БЕЗ markdown, текста вне JSON и без ```."

    private const val JSON_FORMAT =
        "СТРОГИЙ ФОРМАТ: возвращай JSON с тремя полями:\n" +
                "- state: одно из значений 'select_topic', 'collect', 'result'\n" +
                "- answer: краткий ответ на текущий шаг (не более 1 предложения)\n" +
                "- question: уточняющий вопрос, если нужно собрать данные, иначе пустая строка\n" +
                "Пример: {\"state\":\"collect\",\"answer\":\"Ответ модели\",\"question\":\"Уточняющий вопрос\"}. " +
                "Любые другие поля запрещены."

    private const val CHAT_RULES =
        "Ты — тематический агент FSM. Используй ТЕКУЩИЙ контекст для принятия решений:\n" +
                "- state: 'select_topic' → спроси, какая тема интересует пользователя\n" +
                "- collect: задавай 6-7+ коротких уточняющих вопросов для сбора информации. answer = пересказ собранного.\n" +
                "- state: 'collect' → читай answers ({answers.size}/7), задавай следующий вопрос\n" +
                "- result → полный итог\n\n" +
                "КОНТЕКСТ: {\"state\":\"{state}\", \"answers\":{answers}}\n" +
                "Пользователь ответил: {userMessage}\n\n" +
                "ОТВЕТ ТОЛЬКО JSON: {\"state\":\"...\",\"answer\":\"пересказ\",\"question\":\"следующий вопрос\"}"+
                "- JSON должен быть корректным, без лишних полей."

    const val PROMPT_JSON_RESPONSE = JSON_RULES + JSON_FORMAT + CHAT_RULES
}