package com.rafael.ibooks.data.remote.model

data class GeminiGenerateContentRequest(
    val contents: List<Content>
) {
    data class Content(
        val parts: List<Part>
    )

    data class Part(
        val text: String
    )
}