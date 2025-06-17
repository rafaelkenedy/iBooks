package com.rafael.ibooks.data.remote.model

data class GeminiGenerateContentResponse(
    val candidates: List<Candidate>?,
    val promptFeedback: PromptFeedback?
) {
    data class Candidate(
        val content: Content,
    )

    data class Content(
        val parts: List<Part>,
        val role: String
    )

    data class Part(
        val text: String
    )

    data class PromptFeedback(
        val safetyRatings: List<SafetyRating>
    )

    data class SafetyRating(
        val category: String,
        val probability: String
    )
}