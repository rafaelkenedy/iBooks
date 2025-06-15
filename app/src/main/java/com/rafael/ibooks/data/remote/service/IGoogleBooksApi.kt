package com.rafael.ibooks.data.remote.service

import com.rafael.ibooks.data.remote.model.BookItem
import com.rafael.ibooks.data.remote.model.BooksResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface IGoogleBooksApi {
    @GET("volumes")
    suspend fun getBooks(
        @Query("q") query: String,
        @Query("orderBy") orderBy: String? = null,
        @Query("maxResults") maxResults: Int,
        @Query("startIndex") startIndex: Int
    ): BooksResponse

    @GET("volumes/{id}")
    suspend fun getBookById(
        @Path("id") id: String
    ): BookItem
}