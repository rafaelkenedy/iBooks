package com.rafael.ibooks.data.remote.model

import com.google.gson.annotations.SerializedName

data class GeminiGenerateContentResponse(
    @SerializedName("candidates")
    val candidates: List<Candidate>?,
) {
    data class Candidate(
        @SerializedName("content")
        val content: Content
    )

    data class Content(
        @SerializedName("parts")
        val parts: List<Part>
    ) {
        data class Part(
            @SerializedName("text")
            val text: String
        )
    }
}
