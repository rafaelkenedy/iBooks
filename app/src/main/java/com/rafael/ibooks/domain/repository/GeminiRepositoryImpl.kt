package com.rafael.ibooks.domain.repository

import com.rafael.ibooks.data.remote.datasource.IGeminiRemoteDataSource
import com.rafael.ibooks.data.remote.mappers.toDomain
import com.rafael.ibooks.data.remote.model.GeminiGenerateContentRequest

class GeminiRepositoryImpl(
    private val remoteDataSource: IGeminiRemoteDataSource
) : IGeminiRepository {

    override suspend fun generateBookTitles(request: GeminiGenerateContentRequest): List<String> {

        val response = remoteDataSource.generateContent(request)
        val suggestions = response.toDomain()
        return suggestions
    }
}
