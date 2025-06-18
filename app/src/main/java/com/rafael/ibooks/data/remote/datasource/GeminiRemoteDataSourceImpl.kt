package com.rafael.ibooks.data.remote.datasource

import com.rafael.ibooks.data.remote.model.GeminiGenerateContentRequest
import com.rafael.ibooks.data.remote.model.GeminiGenerateContentResponse
import com.rafael.ibooks.data.remote.service.IGeminiApi

class GeminiRemoteDataSourceImpl(
    private val geminiApi: IGeminiApi,
    private val modelName: String = "gemini-2.0-flash"
) : IGeminiRemoteDataSource {

    override suspend fun generateContent(
        request: GeminiGenerateContentRequest
    ): GeminiGenerateContentResponse {
        return geminiApi.generateContent(modelName, request)
    }

}