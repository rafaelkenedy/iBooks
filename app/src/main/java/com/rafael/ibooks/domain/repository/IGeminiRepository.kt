package com.rafael.ibooks.domain.repository

import com.rafael.ibooks.data.remote.model.GeminiGenerateContentRequest

interface IGeminiRepository {
    suspend fun generateBookTitles(request: GeminiGenerateContentRequest): List<String>
}