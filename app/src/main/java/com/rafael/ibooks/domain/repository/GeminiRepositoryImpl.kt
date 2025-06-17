package com.rafael.ibooks.domain.repository

import com.rafael.ibooks.data.remote.datasource.IGeminiRemoteDataSource
import com.rafael.ibooks.data.remote.model.GeminiGenerateContentResponse

class GeminiRepositoryImpl(
    private val remoteDataSource: IGeminiRemoteDataSource
) : IGeminiRepository {

    override suspend fun generateContent(prompt: String): Result<GeminiGenerateContentResponse> {
        val response = remoteDataSource.generateContent(prompt)
        return Result.success(response)
    }
}