package com.rafael.ibooks.data.remote.mappers

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.rafael.ibooks.data.remote.model.GeminiGenerateContentResponse
import com.rafael.ibooks.data.remote.model.SuggestionsResponse

fun GeminiGenerateContentResponse.toDomain(): List<String> {
    val raw = candidates
        ?.firstOrNull()
        ?.content
        ?.parts
        ?.joinToString("") { it.text }
        ?: ""

    val cleaned = raw
        .trim()
        .removePrefix("```json")
        .removePrefix("```")
        .removeSuffix("```")
        .trim()

    return try {
        Gson().fromJson(cleaned, SuggestionsResponse::class.java)
            .suggestions
    } catch (e: JsonSyntaxException) {
        emptyList()
    }
}
