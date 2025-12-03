package com.example.aichathelp.data.mapper

import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import javax.inject.Inject

class JsonResponseMapper @Inject constructor(
    private val json: Json,
) {
    fun cleanRawResponse(raw: String): String {
        val trimmed = raw.trim()
            .removePrefix("```")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()

        val start = trimmed.indexOf('{')
        val end = trimmed.lastIndexOf('}')

        val cleaned = if (start != -1 && end != -1 && start < end) {
            trimmed.substring(start, end + 1)
        } else "{}"


        return if (cleaned.startsWith('[') && cleaned.endsWith(']')) {
            try {
                val array = json.decodeFromString(ListSerializer(JsonElement.serializer()), cleaned)
                json.encodeToString(array.first())
            } catch (_: Exception) {
                "{}"
            }
        } else cleaned

    }

}