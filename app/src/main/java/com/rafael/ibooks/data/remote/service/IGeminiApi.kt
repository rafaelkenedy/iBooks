package com.rafael.ibooks.data.remote.service

import com.rafael.ibooks.data.remote.model.GeminiGenerateContentRequest
import com.rafael.ibooks.data.remote.model.GeminiGenerateContentResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface IGeminiApi {

    @POST("v1beta/models/{modelName}:generateContent")
    suspend fun generateContent(
        @Path("modelName") modelName: String,
        @Body request: GeminiGenerateContentRequest
    ): GeminiGenerateContentResponse
}