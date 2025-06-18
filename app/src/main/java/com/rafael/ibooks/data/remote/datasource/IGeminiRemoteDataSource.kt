package com.rafael.ibooks.data.remote.datasource

import com.rafael.ibooks.data.remote.model.GeminiGenerateContentRequest
import com.rafael.ibooks.data.remote.model.GeminiGenerateContentResponse

interface IGeminiRemoteDataSource {
    suspend fun generateContent(request: GeminiGenerateContentRequest): GeminiGenerateContentResponse
}