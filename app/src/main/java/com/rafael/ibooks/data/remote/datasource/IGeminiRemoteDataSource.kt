package com.rafael.ibooks.data.remote.datasource

import com.rafael.ibooks.data.remote.model.GeminiGenerateContentResponse

interface IGeminiRemoteDataSource {
    suspend fun generateContent(prompt: String): GeminiGenerateContentResponse
}