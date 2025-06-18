package com.rafael.ibooks.data.remote.model

import com.google.gson.annotations.SerializedName

data class GeminiGenerateContentRequest(
    @SerializedName("contents")
    val contents: List<Content>
) {
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
