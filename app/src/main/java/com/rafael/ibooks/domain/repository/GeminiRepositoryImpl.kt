package com.rafael.ibooks.domain.repository

import android.util.Log
import com.rafael.ibooks.data.remote.datasource.IGeminiRemoteDataSource
import com.rafael.ibooks.data.remote.mappers.toDomain
import com.rafael.ibooks.data.remote.model.GeminiGenerateContentRequest

class GeminiRepositoryImpl(
    private val remoteDataSource: IGeminiRemoteDataSource
) : IGeminiRepository {

    companion object {
        private const val TAG = "GeminiRepositoryImpl"
    }

    override suspend fun generateBookTitles(request: GeminiGenerateContentRequest): List<String> {
        // 1. Loga o request completo
        Log.d(TAG, "GenerateBookTitles request: $request")

        // 2. Chama o remoto
        val response = remoteDataSource.generateContent(request)

        // 3. Loga o JSON cru que veio da API
        Log.d(TAG, "Raw response from Gemini API: $response")

        // 4. Converte e loga o resultado mapeado
        val suggestions = response.toDomain()
        Log.d(TAG, "Mapped suggestions list: $suggestions")

        return suggestions
    }
}
