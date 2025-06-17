package com.rafael.ibooks.domain.repository

import com.rafael.ibooks.data.remote.model.GeminiGenerateContentResponse

interface IGeminiRepository {
    suspend fun generateContent(prompt: String): Result<GeminiGenerateContentResponse>
}