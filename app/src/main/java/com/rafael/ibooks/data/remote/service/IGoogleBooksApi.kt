package com.rafael.ibooks.data.remote.service

import com.rafael.ibooks.data.remote.model.BooksResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface IGoogleBooksApi {

    @GET("volumes")
    fun searchBooks(
        @Query("q") query: String,
        @Query("maxResults") maxResults: Int = 10,
        @Query("startIndex") startIndex: Int = 0
    ): Call<BooksResponse>

    @GET("volumes")
    fun getRecentBooks(
        @Query("q") query: String = "*",
        @Query("orderBy") orderBy: String = "newest",
        @Query("maxResults") maxResults: Int = 10,
        @Query("startIndex") startIndex: Int = 0
    ): Call<BooksResponse>
}